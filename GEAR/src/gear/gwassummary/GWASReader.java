package gear.gwassummary;

import gear.ConstValues;
import gear.util.BufferedReader;
import gear.util.Logger;
import gear.util.NewIt;
import gear.util.stat.PrecisePvalue;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;

public class GWASReader {

	public GWASReader(String[] MetaFile, boolean[] FileKeep, String[] field, boolean isQT, boolean isGZ,
			boolean isKeepChr, HashSet<String> Chr) {
		this.field = field;
		this.isQT = isQT;
		this.isGZ = isGZ;
		this.isKeepChr = isKeepChr;

		workingMetaFile = NewIt.newArrayList();
		this.chrSet = Chr;

		for (int i = 0; i < FileKeep.length; i++) {
			if (FileKeep[i]) {
				workingMetaFile.add(MetaFile[i]);
			}
		}

		if (workingMetaFile.size() == 0) {
			Logger.printUserLog("No cohort left. GEAR quit.");
			System.exit(0);
		} else if (workingMetaFile.size() > 1) {
			Logger.printUserLog(workingMetaFile.size() + " cohorts are remained for analysis.\n");
			// Logger.printUserLog("Genomic inflation factors are estimated from the
			// provided p values.\n");
		} else {
			Logger.printUserLog(workingMetaFile.size() + " cohort is remained for analysis.\n");
		}

		gc = new double[workingMetaFile.size()];
		Arrays.fill(gc, 1);

		gcReal = new double[workingMetaFile.size()];
		Arrays.fill(gcReal, 1);

		logit = new boolean[workingMetaFile.size()];
		Arrays.fill(logit, false);

		KeyIdx = new int[workingMetaFile.size()][8];
		for (int i = 0; i < KeyIdx.length; i++) {
			Arrays.fill(KeyIdx[i], -1);
		}
	}

	public void Start(boolean isFrq) {
		this.isFrq = isFrq;
		for (int i = 0; i < workingMetaFile.size(); i++) {
			HashMap<String, MetaStat> m = readMeta(i);
			MStat.add(m);
		}
	}

	public int getCohortNum() {
		return workingMetaFile.size();
	}

	public String[] getMetaFile() {
		return workingMetaFile.toArray(new String[0]);
	}

	public int[][] getKeyIndex() {
		return KeyIdx;
	}

	public int getNumMetaFile() {
		return workingMetaFile.size();
	}

	public ArrayList<HashMap<String, MetaStat>> getMetaStat() {
		return MStat;
	}

	public ArrayList<ArrayList<String>> getMetaSNPArray() {
		return MetaSNPArray;
	}

	public HashMap<String, ArrayList<Integer>> getMetaSNPTable() {
		return MetaSNPTable;
	}

	private HashMap<String, MetaStat> readMeta(int metaIdx) {
		ArrayList<Double> pArray = NewIt.newArrayList();
		ArrayList<Double> pRealArray = NewIt.newArrayList();
		BufferedReader reader = null;
		if (isGZ) {
			reader = BufferedReader.openGZipFile(workingMetaFile.get(metaIdx), "Summary Statistic file");
		} else {
			reader = BufferedReader.openTextFile(workingMetaFile.get(metaIdx), "Summary Statistic file");
		}

		String[] tokens = reader.readTokens();
		int tokenLen = tokens.length;

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equalsIgnoreCase(field[GWASConstant.SNP])) {
				KeyIdx[metaIdx][SNP] = i;
			}
			if (tokens[i].equalsIgnoreCase(field[GWASConstant.CHR])) {
				KeyIdx[metaIdx][CHR] = i;
			}
			if (tokens[i].equalsIgnoreCase(field[GWASConstant.BP])) {
				KeyIdx[metaIdx][BP] = i;
			}
			if (isQT) {
				if (tokens[i].equalsIgnoreCase(field[GWASConstant.BETA])) {
					KeyIdx[metaIdx][BETA] = i;
				}
			} else {
				if (tokens[i].equalsIgnoreCase(field[GWASConstant.BETA])) {
					KeyIdx[metaIdx][BETA] = i;
					logit[metaIdx] = false;
				} else if (tokens[i].equalsIgnoreCase(field[GWASConstant.OR])) {
					KeyIdx[metaIdx][OR] = i;
					logit[metaIdx] = true;
					if (isFrq) {
						logit[metaIdx] = false;
					}
				}
			}
			if (tokens[i].equalsIgnoreCase(field[GWASConstant.SE])) {
				KeyIdx[metaIdx][SE] = i;
			}
			if (tokens[i].equalsIgnoreCase(field[GWASConstant.P])) {
				KeyIdx[metaIdx][P] = i;
			}
			if (tokens[i].equalsIgnoreCase(field[GWASConstant.A1])) {
				KeyIdx[metaIdx][A1] = i;
			}
			if (tokens[i].equalsIgnoreCase(field[GWASConstant.A2])) {
				KeyIdx[metaIdx][A2] = i;
			}
		}

		boolean qFlag = false;

		if (KeyIdx[metaIdx][SNP] == -1) {
			Logger.printUserLog("Cannot find the snp column in " + workingMetaFile.get(metaIdx));
			qFlag = true;
		}
		/*
		 * if (KeyIdx[metaIdx][CHR] == -1) {
		 * Logger.printUserLog("Cannot find the chr column in " + MetaFile[metaIdx]);
		 * qFlag = true; } if (KeyIdx[metaIdx][BP] == -1) {
		 * Logger.printUserLog("Cannot find the bp column in " + MetaFile[metaIdx]);
		 * qFlag = true; }
		 */
		if (KeyIdx[metaIdx][BETA] == -1) {
			Logger.printUserLog("Cannot find the beta/or column in " + workingMetaFile.get(metaIdx));
			qFlag = true;
		}
		if (KeyIdx[metaIdx][SE] == -1) {
			Logger.printUserLog("Cannot find the se value column in " + workingMetaFile.get(metaIdx));
			qFlag = true;
		}
		if (KeyIdx[metaIdx][P] == -1) {
			Logger.printUserLog("Cannot find the p value column in " + workingMetaFile.get(metaIdx));
		}
		if (KeyIdx[metaIdx][A1] == -1) {
			Logger.printUserLog("Cannot find the allele 1 column in " + workingMetaFile.get(metaIdx));
			qFlag = true;
		}
		// if (KeyIdx[metaIdx][7] == -1)
		// {
		// Logger.printUserLog("Cannot find the allele 2 in " + MetaFile[metaIdx]);
		// }

		if (qFlag) {
			Logger.printUserLog("GEAR quit.");
			System.exit(0);
		}

		HashMap<String, MetaStat> sumstat = NewIt.newHashMap();
		ArrayList<String> snpArray = NewIt.newArrayList();
		int total = 0;
		int cnt = 0;
		int cntDup = 0;
		int cntBadChr = 0;
		int cntBadBp = 0;
		int cntBadBeta = 0;
		int cntBadP = 0;
		int cntBadSE = 0;
		int cntBadA1 = 0;
		int cntBadA2 = 0;
		int cntMissSNP = 0;

		while ((tokens = reader.readTokens(tokenLen)) != null) {
			total++;
			if (GWASConstant.isNASNP(tokens[KeyIdx[metaIdx][SNP]])) {
				cntMissSNP++;
				continue;
			}

			if (KeyIdx[metaIdx][CHR] != -1 && ConstValues.isNA(tokens[KeyIdx[metaIdx][CHR]])) {
				cntBadChr++;
				continue;
			}

			if (KeyIdx[metaIdx][BP] != -1 && ConstValues.isNA(tokens[KeyIdx[metaIdx][BP]])) {
				cntBadBp++;
				continue;
			}

			if (ConstValues.isNA(tokens[KeyIdx[metaIdx][BETA]])) {
				cntBadBeta++;
				continue;
			}

			if (ConstValues.isNA(tokens[KeyIdx[metaIdx][SE]])) {
				cntBadSE++;
				continue;
			}

			if (Float.parseFloat(tokens[KeyIdx[metaIdx][SE]]) <= 0) {
				cntBadSE++;
				continue;
			}

			if (KeyIdx[metaIdx][P] != -1 && ConstValues.isNA(tokens[KeyIdx[metaIdx][P]])) {
				cntBadP++;
				continue;
			} else if (KeyIdx[metaIdx][P] != -1 && !ConstValues.isNA(tokens[KeyIdx[metaIdx][P]])) {// calculate gc
				pArray.add(new Double(Double.parseDouble(tokens[KeyIdx[metaIdx][P]])));
			}

			if (tokens[KeyIdx[metaIdx][A1]].length() != 1) {
				cntBadA1++;
				continue;
			}

			if (KeyIdx[metaIdx][A2] != -1 && tokens[KeyIdx[metaIdx][A2]].length() != 1) {
				cntBadA2++;
				continue;
			}

			double p1 = getRealP(tokens, metaIdx, logit[metaIdx]);
			if (p1 >= 0) {
				pRealArray.add(p1);
			}

			MetaStat ms = null;
			if (KeyIdx[metaIdx][P] != -1) {
				ms = new MetaStat(tokens[KeyIdx[metaIdx][SNP]], Float.parseFloat(tokens[KeyIdx[metaIdx][BETA]]),
						Float.parseFloat(tokens[KeyIdx[metaIdx][SE]]), Double.parseDouble(tokens[KeyIdx[metaIdx][P]]),
						tokens[KeyIdx[metaIdx][A1]].charAt(0), logit[metaIdx]);
			} else {
				ms = new MetaStat(tokens[KeyIdx[metaIdx][SNP]], Float.parseFloat(tokens[KeyIdx[metaIdx][BETA]]),
						Float.parseFloat(tokens[KeyIdx[metaIdx][SE]]), Double.NaN,
						tokens[KeyIdx[metaIdx][A1]].charAt(0), logit[metaIdx]);
			}
			if (KeyIdx[metaIdx][CHR] != -1) {
				if (tokens[KeyIdx[metaIdx][CHR]].equalsIgnoreCase("X")) {
					ms.setChr(23);
				} else if (tokens[KeyIdx[metaIdx][CHR]].equalsIgnoreCase("Y")) {
					ms.setChr(24);
				} else if (tokens[KeyIdx[metaIdx][CHR]].equalsIgnoreCase("XY")) {
					ms.setChr(25);
				} else if (tokens[KeyIdx[metaIdx][CHR]].equalsIgnoreCase("MT")) {
					ms.setChr(26);
				} else {
					int chr = -1;
					try {
						chr = Integer.parseInt(tokens[KeyIdx[metaIdx][CHR]]);
					} catch (NumberFormatException e) {
						Logger.printUserLog(e.toString() + " in line " + total + " in '" + workingMetaFile.get(metaIdx)
								+ ".' is a bad value for chromosome. Skipped this marker.");
						continue;
					}
					ms.setChr(chr);
				}

				if (isKeepChr) {
					if (chrSet != null && !chrSet.contains((new Integer(ms.getChr()).toString()))) {
						continue;
					}
				} else {
					if (chrSet != null && chrSet.contains((new Integer(ms.getChr()).toString()))) {
						continue;
					}
				}
			}
			if (KeyIdx[metaIdx][BP] != -1) {
				long bp = -1;
				try {
					bp = Long.parseLong(tokens[KeyIdx[metaIdx][BP]]);
				} catch (NumberFormatException e) {
					Logger.printUserLog(e.toString() + " in line " + total + " in '" + workingMetaFile.get(metaIdx)
							+ "' is a bad value for position. Skipped this marker.");
					continue;
				}
				ms.setBP(bp);
			}
			if (KeyIdx[metaIdx][A2] != -1) {
				ms.setA2(tokens[KeyIdx[metaIdx][A2]].charAt(0));
			}

			if (sumstat.containsKey(ms.getSNP())) {
				Logger.printUserLog(
						"Warning: Marker '" + ms.getSNP() + "' duplicated input, first instance used, others skipped.");
				cntDup++;
			} else {
				sumstat.put(ms.getSNP(), ms);
				snpArray.add(ms.getSNP());

				if (MetaSNPTable.containsKey(ms.getSNP())) {
					ArrayList<Integer> snpCnt = MetaSNPTable.get(ms.getSNP());
					snpCnt.set(metaIdx, 1);
					Integer Int = snpCnt.get(snpCnt.size() - 1);
					Int++;
					snpCnt.set(snpCnt.size() - 1, Int);
				} else {
					ArrayList<Integer> snpCnt = NewIt.newArrayList();
					snpCnt.ensureCapacity(workingMetaFile.size() + 1);
					for (int ii = 0; ii < workingMetaFile.size() + 1; ii++) {
						snpCnt.add(0);
					}
					snpCnt.set(metaIdx, 1);
					snpCnt.set(snpCnt.size() - 1, 1);
					MetaSNPTable.put(ms.getSNP(), snpCnt);
				}
				cnt++;
			}
		}
		reader.close();

		if (cntMissSNP > 0) {
			String lc = cntMissSNP == 1 ? "locus" : "loci";
			Logger.printUserLog("Removed " + cntMissSNP + " " + lc + " due to bad marker name(s)");
		}

		if (cntBadChr > 0) {
			String lc = cntBadChr == 1 ? "locus" : "loci";
			Logger.printUserLog("Removed " + cntBadChr + " " + lc + " due to incorrect chromosome(s).");
		}

		if (cntBadBp > 0) {
			String lc = cntBadBp == 1 ? "locus" : "loci";
			Logger.printUserLog("Removed " + cntBadBp + " " + lc + " due to incorrect physical position(s).");
		}

		if (cntBadBeta > 0) {
			String lc = cntBadBeta == 1 ? "locus" : "loci";
			Logger.printUserLog("Removed " + cntBadBeta + " " + lc + " due to incorrect effect(s).");
		}

		if (cntBadSE > 0) {
			String lc = cntBadSE == 1 ? "locus" : "loci";
			Logger.printUserLog("Removed " + cntBadSE + " " + lc + " due to incorrect se.");
		}

		if (cntBadP > 0) {
			String lc = cntBadP == 1 ? "locus" : "loci";
			Logger.printUserLog("Removed " + cntBadP + " " + lc + " due to incorrect p value(s).");
		}

		if (cntBadA1 > 0) {
			String lc = cntBadA1 == 1 ? "locus" : "loci";
			Logger.printUserLog("Removed " + cntBadA1 + " " + lc + " due to bad a1 allele(s).");
		}

		if (cntBadA2 > 0) {
			String lc = cntBadA2 == 1 ? "locus" : "loci";
			Logger.printUserLog("Removed " + cntBadA2 + " " + lc + " due to bad a2 allele(s).");
		}

		if (cntDup > 0) {
			String lc = cntDup == 1 ? "locus" : "loci";
			Logger.printUserLog("Removed " + cntDup + " duplicated " + lc);
		}

		MetaSNPArray.add(snpArray);
		gc[metaIdx] = getGC(pArray);
		gcReal[metaIdx] = getRealGC(pRealArray);
		if (cnt == 0) {
			Logger.printUserLog("Did not find any summary statistics from '" + workingMetaFile.get(metaIdx) + ".'");
			System.exit(0);
		} else {
			Logger.printUserLog("Read " + cnt + " (of " + total + ") summary statistics from '"
					+ workingMetaFile.get(metaIdx) + ".'\n");
		}

		return sumstat;
	}

	private double getGC(ArrayList<Double> pArray) {
		double lambda = 1;
		if (pArray.size() > 0) {
			ChiSquaredDistributionImpl chiDis = new ChiSquaredDistributionImpl(1);

			Collections.sort(pArray);
			double p = 0.5;
			if (pArray.size() % 2 == 0) {
				p = 1 - (pArray.get(pArray.size() / 2) + pArray.get(pArray.size() / 2 + 1)) / 2;
			} else {
				p = 1 - pArray.get((pArray.size() + 1) / 2);
			}
			try {
				lambda = chiDis.inverseCumulativeProbability(p) / ChiMedianConstant;
			} catch (MathException e) {
				Logger.printUserError(e.toString());
			}
			DecimalFormat fmt = new DecimalFormat("0.000");

			Logger.printUserLog("Genomic control factor (lambda_gc) is calculated from the provided p values: "
					+ fmt.format(lambda));
		} else {
			Logger.printUserLog("No p values provided. Genomic control factor is set to 1.");
		}
		return lambda;
	}

	private double getRealGC(ArrayList<Double> pArray) {
		double lambda = 1;
		if (pArray.size() > 0) {
			ChiSquaredDistributionImpl chiDis = new ChiSquaredDistributionImpl(1);

			Collections.sort(pArray);
			double p = 0.5;
			if (pArray.size() % 2 == 0) {
				p = 1 - (pArray.get(pArray.size() / 2) + pArray.get(pArray.size() / 2 + 1)) / 2;
			} else {
				p = 1 - pArray.get((pArray.size() + 1) / 2);
			}
			try {
				lambda = chiDis.inverseCumulativeProbability(p) / ChiMedianConstant;
			} catch (MathException e) {
				Logger.printUserError(e.toString());
			}
			DecimalFormat fmt = new DecimalFormat("0.000");

			Logger.printUserLog("Genomic control factor (lambda_gc) is calculated from the provided beta and se: "
					+ fmt.format(lambda));
		} else {
			Logger.printUserLog("No p values provided. Genomic control factor is set to 1.");
		}
		return lambda;
	}

	public double[] GetGC() {
		return gc;
	}

	public double[] GetRealGC() {
		return gcReal;
	}

	public double getRealP(String[] tokens, int metaIdx, boolean logit) {
		double z = 0;
		double p = -1;
		if (!ConstValues.isNA(tokens[KeyIdx[metaIdx][BETA]]) && !ConstValues.isNA(tokens[KeyIdx[metaIdx][SE]])) {
			if (Float.parseFloat(tokens[KeyIdx[metaIdx][SE]]) > 0) {
				double b = Double.parseDouble(tokens[KeyIdx[metaIdx][BETA]]);
				double se = Double.parseDouble(tokens[KeyIdx[metaIdx][SE]]);
				z = logit ? Math.log(b) / se : b / se;
				p = PrecisePvalue.getPvalue4Z_2Tail(z);
			}
		}
		return p;
	}

	public ArrayList<String> getWorkingMetaFile() {
		return workingMetaFile;
	}

	private boolean isKeepChr = true;
	private HashSet<String> chrSet;
	private String[] field;
	private boolean isQT;
	private boolean isGZ;
	private boolean[] logit;
	public static int SNP = 0, CHR = 1, BP = 2, BETA = 3, OR = 3, SE = 4, P = 5, A1 = 6, A2 = 7;
	private int[][] KeyIdx; // snp, chr, bp, beta, se, p, a1, a2
	private ArrayList<String> workingMetaFile;
	private ArrayList<HashMap<String, MetaStat>> MStat = NewIt.newArrayList();
	private ArrayList<ArrayList<String>> MetaSNPArray = NewIt.newArrayList();
	private HashMap<String, ArrayList<Integer>> MetaSNPTable = NewIt.newHashMap();

	private double[] gc;
	private double[] gcReal;
	private double ChiMedianConstant = 0.4549364;
	private boolean isFrq;
	// private int[] keepCohortIdx;

}
