package gear.subcommands.dnafingerprint;

import gear.subcommands.CommandArguments;
import gear.util.FileUtil;

public class DFPCommandArguments extends CommandArguments
{

	public void setBFile2(String bfile2) 
	{
		this.bfile2 = bfile2;
		FileUtil.exists(new String(this.bfile2 + ".bed"));
		FileUtil.exists(new String(this.bfile2 + ".bim"));
		FileUtil.exists(new String(this.bfile2 + ".fam"));

		this.isbFile2 = true;
	}

	public boolean isbFile2()
	{
		return isbFile2;
	}

	public String getBFile2()
	{
		return bfile2;
	}

	public String getBed2()
	{
		return new String(bfile2 + ".bed");
	}
	
	public String getBim2()
	{
		return new String(bfile2 + ".bim");
	}
	
	public String getFam2()
	{
		return new String(bfile2 + ".fam");
	}

	public void setLowCutoff(double lowCutoff)
	{
		this.lowCutoff = lowCutoff;
	}
	
	public double getLowCutoff()
	{
		return lowCutoff;
	}
	
	public void setHighCutoff(double highCutoff)
	{
		this.highCutoff = highCutoff;
	}
	
	public double getHighCutoff()
	{
		return highCutoff;
	}

	public void setNumMarker(long numMarker)
	{
		this.numMarker = numMarker;
		this.numMarkerFlag = true;
	}
	
	public long getNumMarker()
	{
		return numMarker;
	}

	public boolean getNumMarkerFlag()
	{
		return numMarkerFlag;
	}

	private String bfile2 = null;
	private boolean isbFile2;

	private double lowCutoff;
	private double highCutoff;
	private long numMarker;
	private boolean numMarkerFlag = false;
}
