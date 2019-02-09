package gear.subcommands.locus;

import java.io.PrintStream;
import java.text.DecimalFormat;

import gear.family.GenoMatrix.GenotypeMatrix;
import gear.family.pedigree.file.BEDReader;
import gear.family.pedigree.file.MapFile;
import gear.family.pedigree.file.SNP;
import gear.family.plink.PLINKBinaryParser;
import gear.family.plink.PLINKParser;
import gear.family.qc.colqc.SNPFilter;
import gear.family.qc.rowqc.SampleFilter;
import gear.subcommands.CommandArguments;
import gear.subcommands.CommandImpl;
import gear.util.FileUtil;
import gear.util.Logger;
import gear.util.pop.PopStat;

public class LocusCommandImpl extends CommandImpl {
	private GenotypeMatrix pGM;
	private double[][] allelefreq;
	private double[] allelevar;
	private double[][] genoCnt;
	private LocusCommandArguments locusArgs;
	private MapFile map;
	private SNPFilter snpFilter;
	private BEDReader bed;
	private PrintStream resultFile;
	private DecimalFormat fmt1 = new DecimalFormat("0.0000");
	private DecimalFormat fmt2 = new DecimalFormat("0.00E000");

	@Override
	public void execute(CommandArguments cmdArgs) {
		locusArgs = (LocusCommandArguments) cmdArgs;

		PLINKParser pp = PLINKParser.create(locusArgs);
		pp.parseSmallFiles();
		map = pp.getMapData();
		snpFilter = pp.getSNPFilter();

		resultFile = FileUtil.CreatePrintStream(locusArgs.getOutRoot() + ".locus");
		resultFile.println("SNP\tCHR\tBP\tRefAllele\tAltAllele\tFreq\tVar\tEVar\tAA\tAa\taa\tnChr");

		if (pp.getPedigreeData() instanceof BEDReader) {
			bed = (BEDReader) pp.getPedigreeData();
			if (bed.IsSnpMajor()) {
				executeBedSnpMajor();
				return;
			}
		}

		pp.parsePedigreeFile();
		SampleFilter sf = new SampleFilter(pp.getPedigreeData(), cmdArgs);
		pGM = new GenotypeMatrix(sf.getSample(), pp.getMapData(), cmdArgs);

		allelefreq = PopStat.calAlleleFrequency(pGM);
		allelevar = PopStat.calGenoVariance(pGM);
		genoCnt = PopStat.calGenoFrequency(pGM, false);
		printResult();
	}
	
	private void executeBedSnpMajor() {
		int numMarkers = map.getMarkerNumberOriginal();
		int numSamples = bed.getNumIndividuals();
		int workingSnpIndex = 0;
		for (int i = 0; i < numMarkers; ++i) {
			if (snpFilter.isSnpIncluded(i)) {
				int genoCnt_AA = 0;
				int genoCnt_Aa = 0;
				int genoCnt_aa = 0;
				int missingCnt = 0;
				int sum = 0;
				int squareSum = 0;
				for (int j = 0; j < numSamples; j += 4) {
					int nextByte = bed.readNextByte();
					for (int k = 0; k < 8; k += 2) {
						// TODO: sample filter
						int genotype = (nextByte >> k) & 0b11;
						switch (genotype) {
						case PLINKBinaryParser.HOMOZYGOTE_FIRST:
							++genoCnt_AA;
							break;
						case PLINKBinaryParser.HETEROZYGOTE:
							++genoCnt_Aa;
							sum += 1;
							squareSum += 1;
							break;
						case PLINKBinaryParser.HOMOZYGOTE_SECOND:
							++genoCnt_aa;
							sum += 2;
							squareSum += 4;
							break;
						case PLINKBinaryParser.MISSING_GENOTYPE:
							++missingCnt;
							break;
						}
					}
				}
				int validSampleCnt = numSamples - missingCnt;
				double variance = 0;
				if (validSampleCnt > 2) {
					double average = (double)sum / validSampleCnt;
					variance = (squareSum - validSampleCnt * average * average) / (validSampleCnt - 1);
				}
				double alleleFreq0 = (double)((genoCnt_AA << 1) + genoCnt_Aa) / ((genoCnt_AA + genoCnt_Aa + genoCnt_aa) << 1);
				double alleleFreq1 = 1.0 - alleleFreq0;
				double eVariance = calculateEVariance(alleleFreq0, alleleFreq1);
				SNP snp = map.getSNP(workingSnpIndex++);
				printResultOfSNP(snp, alleleFreq0, variance, eVariance, genoCnt_AA, genoCnt_Aa, genoCnt_aa);
			} else {
				bed.skipOneRow();
			}
		}
		resultFile.close();
		Logger.printUserLog("Save results to " + locusArgs.getOutRoot() + ".locus.");
	}
	
	private double calculateEVariance(double alleleFreq0, double alleleFreq1) {
		return locusArgs.isInbred() ? 4 * alleleFreq0 * alleleFreq1 : 2 * alleleFreq0 * alleleFreq1;
	}

	private void printResult() {
		for (int i = 0; i < pGM.getSNPList().size(); i++) {
			SNP snp = pGM.getSNPList().get(i);
			double eVar = calculateEVariance(allelefreq[i][0], allelefreq[i][1]);
			printResultOfSNP(
					snp,
					allelefreq[i][0],
					allelevar[i],
					eVar,
					(int)genoCnt[i][0],
					(int)genoCnt[i][1],
					(int)genoCnt[i][2]);
		}
		resultFile.close();
		Logger.printUserLog("Save results to " + locusArgs.getOutRoot() + ".locus.");
	}
	
	private void printResultOfSNP(
			SNP snp,
			double alleleFreq0,
			double alleleVar,
			double eVar,
			int genoCnt_AA,
			int genoCnt_Aa,
			int genoCnt_aa) {
		resultFile.println(snp.getName() + "\t" + snp.getChromosome() + "\t" + snp.getPosition() + "\t"
				+ snp.getFirstAllele() + "\t" + snp.getSecAllele() + "\t"
				+ (alleleFreq0 > 0.0001 ? fmt1.format(alleleFreq0) : fmt2.format(alleleFreq0)) + "\t"
				+ (alleleVar > 0.0001 ? fmt1.format(alleleVar) : fmt2.format(alleleVar)) + "\t"
				+ (eVar > 0.001 ? fmt1.format(eVar) : fmt2.format(eVar)) + "\t"
				+ genoCnt_AA + "\t"
				+ genoCnt_Aa + "\t"
				+ genoCnt_aa + "\t"
				+ ((genoCnt_AA + genoCnt_Aa + genoCnt_aa) << 1));
	}
}
