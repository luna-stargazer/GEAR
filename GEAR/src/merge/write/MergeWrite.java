package merge.write;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;


import family.pedigree.PersonIndex;
import family.pedigree.file.SNP;
import family.pedigree.genotype.BPerson;
import family.plink.PLINKBinaryParser;
import family.plink.PLINKParser;
import family.qc.rowqc.SampleFilter;
import gear.Parameter;
import gear.util.FileProcessor;
import gear.util.Logger;

public class MergeWrite {
	private byte byte1 = 108;
	private byte byte2 = 27;
	private byte byte3 = 1;
	
	private ArrayList<PersonIndex> PersonTable;
	private DataOutputStream os = null;
	private Parameter par;
	private ArrayList<SNP> snpList;

	public MergeWrite (Parameter p) {
		par = p;
		PLINKParser pp = null;
		if (Parameter.INSTANCE.getFileParameter().isSet()) {
			pp = new PLINKParser (Parameter.INSTANCE.getFileParameter().getPedFile(),
					              Parameter.INSTANCE.getFileParameter().getMapFile());
		}
		if (Parameter.INSTANCE.getBfileParameter(0).isSet()) {
			pp = new PLINKBinaryParser (Parameter.INSTANCE.getBfileParameter(0).getBedFile(),
					                    Parameter.INSTANCE.getBfileParameter(0).getBimFile(),
					                    Parameter.INSTANCE.getBfileParameter(0).getFamFile());
		} else {
			Logger.printUserError("No input files.");
			System.exit(1);
		}
		pp.Parse();
		SampleFilter sf = new SampleFilter(pp.getPedigreeData(), pp.getMapData());

		snpList = pp.getMapData().getMarkerList();
		PersonTable = sf.getSample();
	}

	public MergeWrite (ArrayList<PersonIndex> pt, ArrayList<SNP> sl) {
		snpList = sl;
		PersonTable = pt;
	}

	public void WriteFile() {
		StringBuffer sbim = new StringBuffer();
		sbim.append(par.out);
		sbim.append(".bim");
		PrintStream pbim = FileProcessor.CreatePrintStream(sbim.toString());
		for (Iterator<SNP> e = snpList.iterator(); e.hasNext(); ) {
			SNP snp = e.next();
			pbim.append(snp.getChromosome() + "\t" +snp.getName() + "\t" + snp.getDistance() + "\t" + snp.getPosition() + "\t" + snp.getRefAllele() + "\t" + snp.getSecAllele() + "\n");
		}
		pbim.close();
		
		StringBuffer sfam = new StringBuffer();
		sfam.append(par.out);
		sfam.append(".fam");
		PrintStream pfam = FileProcessor.CreatePrintStream(sfam.toString());		
		for (Iterator<PersonIndex> e = PersonTable.iterator(); e.hasNext(); ) {
			PersonIndex per = e.next();
			BPerson bp = per.getPerson();
			pfam.append(bp.getFamilyID() + "\t" + bp.getPersonID() + "\t" + bp.getDadID() + "\t" + bp.getMomID() + "\t" + bp.getGender() + "\t" + bp.getAffectedStatus() + "\n");
		}
		pfam.close();
		
		StringBuffer sbed = new StringBuffer();
		sbed.append(par.out);
		sbed.append(".bed");
		try {
			os = new DataOutputStream(new FileOutputStream(sbed.toString()));
		} catch (FileNotFoundException e) {
			Logger.handleException(e, "Cannot create the file '" + sbed.toString() + "'.");
		}

		try {
			os.writeByte(byte1);
			os.writeByte(byte2);
			os.writeByte(byte3);
			
			for (int i = 0; i < snpList.size(); i++) {
				byte gbyte = 0;
				int idx = 0;

				int posByte = i >> BPerson.shift;
				int posBite = (i & 0xf) << 1;

				for (int j = 0; j < PersonTable.size(); j++) {
					PersonIndex pi = PersonTable.get(j);
					BPerson bp = pi.getPerson();
					byte g = bp.getOriginalGenotypeScore(posByte, posBite);

					g <<= 2 * idx;
					gbyte |= g;
					idx++;

					if (j != (PersonTable.size() - 1) ) {
						if (idx == 4) {
							os.writeByte(gbyte);
							gbyte = 0;
							idx = 0;
						}
					} else {
						os.writeByte(gbyte);
					}
				}
			}
			os.close();
		} catch (IOException e) {
			Logger.handleException(e, "An exception occurred during writing the file '" + sbed.toString() + "'.");
		}
	}

	public void WriteFile(String out) {
		StringBuffer sbim = new StringBuffer();
		sbim.append(out);
		sbim.append(".bim");
		PrintStream pbim = FileProcessor.CreatePrintStream(sbim.toString());
		for (Iterator<SNP> e = snpList.iterator(); e.hasNext(); ) {
			SNP snp = e.next();
			pbim.append(snp.getChromosome() + "\t" +snp.getName() + "\t" + snp.getDistance() + "\t" + snp.getPosition() + "\t" + snp.getRefAllele() + "\t" + snp.getSecAllele() + "\n");
		}
		pbim.close();
		
		StringBuffer sfam = new StringBuffer();
		sfam.append(out);
		sfam.append(".fam");
		PrintStream pfam = FileProcessor.CreatePrintStream(sfam.toString());		
		for (Iterator<PersonIndex> e = PersonTable.iterator(); e.hasNext(); ) {
			PersonIndex per = e.next();
			BPerson bp = per.getPerson();
			pfam.append(bp.getFamilyID() + "\t" + bp.getPersonID() + "\t" + bp.getDadID() + "\t" + bp.getMomID() + "\t" + bp.getGender() + "\t" + bp.getAffectedStatus() + "\n");
		}
		pfam.close();
		
		StringBuffer sbed = new StringBuffer();
		sbed.append(out);
		sbed.append(".bed");
		try {
			os = new DataOutputStream(new FileOutputStream(sbed.toString()));
		} catch (FileNotFoundException e) {
			Logger.handleException(e, "Cannot create file '" + sbed.toString() + "'.");
		}

		try {
			os.writeByte(byte1);
			os.writeByte(byte2);
			os.writeByte(byte3);
			
			for (int i = 0; i < snpList.size(); i++) {
				byte gbyte = 0;
				int idx = 0;

				int posByte = i >> BPerson.shift;
				int posBite = (i & 0xf) << 1;

				for (int j = 0; j < PersonTable.size(); j++) {
					PersonIndex pi = PersonTable.get(j);
					BPerson bp = pi.getPerson();
					byte g = bp.getOriginalGenotypeScore(posByte, posBite);

					g <<= 2 * idx;
					gbyte |= g;
					idx++;

					if (j != (PersonTable.size() - 1) ) {
						if (idx == 4) {
							os.writeByte(gbyte);
							gbyte = 0;
							idx = 0;
						}
					} else {
						os.writeByte(gbyte);
					}
				}
			}
			os.close();
		} catch (IOException e) {
			Logger.handleException(e, "An exception occurred during writing the file'" + sbed.toString() + "'.");
		}
	}

}