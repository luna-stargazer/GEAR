package gear.subcommands.oath.synthesize;

import java.util.ArrayList;

import gear.subcommands.CommandArguments;
import gear.subcommands.oath.OATHConst;
import gear.util.BufferedReader;
import gear.util.FileUtil;
import gear.util.Logger;
import gear.util.NewIt;

public class SynthCommandArguments extends CommandArguments 
{

	public void setMetaBatch(String batch)
	{
		FileUtil.exists(batch);
		md = NewIt.newArrayList();
		BufferedReader reader = BufferedReader.openTextFile(batch, "MetaBatch");

//		Logger.printUserLog("Checking the summary statistic files...");
		String[] tokens = null;
		while((tokens = reader.readTokens())!=null)
		{
			FileUtil.exists(tokens[0]);
			md.add(tokens[0]);
		}
//		Logger.printUserLog("Found all of " + md.size() + " files.");
	}

	public void setGZ(boolean flag) 
	{
		isGZ = flag;
	}

	public boolean isGZ()
	{
		return isGZ;
	}

	public String[] getMetaFile() 
	{
		return md.toArray(new String[0]);
	}

	public void setChr(String chr)
	{
		this.chr = Integer.parseInt(chr); 
		chrFlag = true;
	}

	public int getChr()
	{
		return chr;
	}

	public boolean isChr()
	{
		return chrFlag;
	}

	public String[] getKeys() 
	{
		return field;
	}

	public void setCMFile(String cm) 
	{
		FileUtil.exists(cm);
		cmFile = cm;
	}
	
	public String getCMFile()
	{
		return cmFile;
	}

	public void setN(String n) 
	{
		N = Integer.parseInt(n);
		if(N < 10)
		{
			Logger.printUserLog("Too small sample size.");
			System.exit(1);
		}
	}

	public int getN()
	{
		return N;
	}
	
	private String cmFile;
	private ArrayList<String> md;
	private int chr = 1;
	private boolean chrFlag = false;
	private boolean isGZ = false;
	private int N = 1000;
	private String[] field = {OATHConst.SNP, OATHConst.CHR, OATHConst.BP, OATHConst.RefAle, OATHConst.AltAle, OATHConst.Freq, OATHConst.Vg, OATHConst.BETA, OATHConst.SE, OATHConst.CHI, OATHConst.P};

}