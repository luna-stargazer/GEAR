package family.plink;

import java.io.IOException;

import family.pedigree.file.MapFile;
import family.pedigree.file.TransposePedigreeReader;
import gear.util.Logger;

public class PLINKTransposeParser extends PLINKParser {

	protected String FamFile;
	public PLINKTransposeParser(String ped, String Fam, String map) {
		super(ped, null);
		FamFile = Fam;
		// TODO Auto-generated constructor stub
	}

	public void Parse() {
		mapData = new MapFile(null);
		pedData = new TransposePedigreeReader(pedigreeFile, FamFile, mapData);
		pedData.setHeader(false);

		pedData.setHeader(false);
		ParsePedFile();

		mapData.setPolymorphismMarker(pedData.getPolymorphism());
		pedData.cleanup();
	}

	public void ParsePedFile() {
		try {
			pedData.parseLinkage(pedigreeFile, 0, snpFilter.getWorkingSNP());
		} catch (IOException e) {
			Logger.handleException(e, "An exception occurred when parsing the pedgree files.");
		}
	}
}