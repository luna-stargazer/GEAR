package gear;

import gear.util.FileUtil;
import gear.util.Logger;
import gear.util.NewIt;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

// singleton implemented in enum way
public enum CmdArgs
{
	INSTANCE;

	private CommandLine cmdLine;

	private Options ops = new Options();

	private CommandLineParser parser = new PosixParser();

	@SuppressWarnings("static-access")
	private CmdArgs()
	{
		ops = new Options();
		fileArgs = new FileArgs();
		bfileArgs = new BFileArgs[2];
		bfileArgs[0] = new BFileArgs("PLINK format binary input file", "bfile");
		bfileArgs[1] = new BFileArgs(
				"The second PLINK format binary input file", "bfile2");

		// real-check

		ops.addOption(OptionBuilder.withDescription("linear")
				.create(cmd_linear));

		ops.addOption(OptionBuilder.withDescription("logit").create(cmd_logit));

		// simulation real data
		ops.addOption(OptionBuilder.withDescription("gwas data simulations ")
				.create(cmd_bsimu));

		ops.addOption(OptionBuilder.withDescription("gwas simulation seed ").hasArg()
				.create(cmd_seed));

		ops.addOption(OptionBuilder.withLongOpt(cmd_simu_rep_long)
				.withDescription("gwas simulation replication ").hasArg()
				.create(cmd_simu_rep));

		ops.addOption(OptionBuilder.withLongOpt(cmd_simu_causal_loci_long)
				.withDescription("gwas simulation causal loci ").hasArg()
				.create(cmd_simu_causal_loci));

		ops.addOption(OptionBuilder.withLongOpt(cmd_simu_rnd_causal_loci_long)
				.withDescription("gwas simulation casual loci number ")
				.hasArg().create(cmd_simu_rnd_causal_loci));

		ops.addOption(OptionBuilder.withLongOpt(cmd_simu_hsq_long)
				.withDescription("gwas simulation heritability ").hasArg()
				.create(cmd_simu_hsq));

		ops.addOption(OptionBuilder.withLongOpt(cmd_simu_qt_long)
				.withDescription("gwas simulate quantitative traits ").hasArg()
				.create(cmd_simu_qt));

		ops.addOption(OptionBuilder.withLongOpt(cmd_simu_order_long)
				.withDescription("order SNP effects ascendingly ")
				.create(cmd_simu_order));

		ops.addOption(OptionBuilder.withLongOpt(cmd_simu_cc_long)
				.withDescription("gwas simulate case-control ").hasArg()
				.create(cmd_simu_cc));

		ops.addOption(OptionBuilder.withLongOpt(cmd_simu_k_long)
				.withDescription("gwas prevalence of the binary trait ")
				.hasArg().create(cmd_simu_k));

		// ibd
		ops.addOption(OptionBuilder.withLongOpt(cmd_quickibd_long).withDescription("quick ibd for dense markers" ).create(cmd_quickibd));

		ops.addOption(OptionBuilder.withLongOpt(cmd_helink_long).withDescription("he linkage" ).create(cmd_helink));
		ops.addOption(OptionBuilder.withLongOpt(cmd_hejoint_long).withDescription("he joint linkage" ).create(cmd_hejoint));
		ops.addOption(OptionBuilder.withDescription("ibd score file" ).hasArg().create(cmd_ibd));
		ops.addOption(OptionBuilder.withDescription("squared difference" ).create(cmd_sd));
		ops.addOption(OptionBuilder.withDescription("squared sum" ).create(cmd_ss));
		ops.addOption(OptionBuilder.withDescription("cross-product" ).create(cmd_cp));
		ops.addOption(OptionBuilder.withDescription("reml" ).create(cmd_reml));

		// nontransmitted
		ops.addOption(OptionBuilder.withDescription("nontransmitted ")
				.create(cmd_nontrans));

		ops.addOption(OptionBuilder.withLongOpt(cmd_nontrans_cases_long)
				.withDescription("nontransmitted filter cases ")
				.create(cmd_nontrans_cases));

		ops.addOption(OptionBuilder.withLongOpt(cmd_nontrans_controls_long)
				.withDescription("nontransmitted filter controls ")
				.create(cmd_nontrans_controls));

		// simulation polygenic model

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_poly_loci_long)
				.withDescription(	"number of polygenic loci, defualt= " + polyLoci)
				.hasArg().create(cmd_poly_loci));

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_poly_loci_null_long)
				.withDescription(	"number of null polygenic loci, defualt= " + polyLoci)
				.hasArg().create(cmd_poly_loci_null));

		ops.addOption(OptionBuilder.withLongOpt(cmd_poly_LD_long)
				.withDescription("LD (correlation), defualt= " + polyLD)
				.hasArg().create(cmd_poly_LD));

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_poly_U_long)
				.withDescription("polygenic model has Uniform Effect? " + polyU)
				.create(cmd_poly_U));

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_poly_freq_long)
				.withDescription(	"minor allele frequency for polygenic model? " + polyFreq)
				.hasArg().create(cmd_poly_freq));

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_poly_effect_long)
				.withDescription(	"effect for polygenic model? " + polyEffectFile)
				.hasArg().create(cmd_poly_effect));

		// pop stat
		ops.addOption(OptionBuilder.withDescription("calculate MAF frequency ")
				.create(cmd_freq));

		ops.addOption(OptionBuilder.withLongOpt(cmd_geno_freq_long)
				.withDescription("calculate genotype frequency ")
				.create(cmd_geno_freq));

		ops.addOption(OptionBuilder.withDescription("calculate fst ").hasArg()
				.create(cmd_fst));

		//watchdog
		ops.addOption(OptionBuilder.withDescription("meta-watchdog power calculator alpha").hasArg().create(cmd_dog_alpha));

		ops.addOption(OptionBuilder.withDescription("meta-analysis watchdog ").create(cmd_watchdog));

		ops.addOption(OptionBuilder.withDescription("meta-analysis set1 ").hasArg().create(cmd_set1));

		ops.addOption(OptionBuilder.withDescription("meta-analysis set2 ").hasArg().create(cmd_set2));

		ops.addOption(OptionBuilder.withDescription("meta-analysis alpha ").hasArg().create(cmd_dog_cutoff));

		//impute
		ops.addOption(OptionBuilder.withDescription("convert impute probability to best-guess genotypes").withLongOpt(cmd_impute_format_long).create());

		ops.addOption(OptionBuilder.withDescription("convert impute probability to best-guess batch file").withLongOpt(cmd_impute_batch_long).hasArg().create());

		// snp
		ops.addOption(OptionBuilder.withLongOpt(cmd_naive_imputation_long)
				.withDescription("naive imputation ")
				.create(cmd_naive_imputation));

		// snp selection
		ops.addOption(OptionBuilder.withDescription("select chromosomes")
				.hasArgs().create(cmd_chr));
		ops.addOption(OptionBuilder.withDescription("select snps").hasArgs()
				.create(cmd_snps));

		// individual selection

		ops.addOption(OptionBuilder.withDescription("remove individuals")
				.hasArg().create(cmd_remove));

		ops.addOption(OptionBuilder.withDescription("keep individuals")
				.hasArg().create(cmd_keep));

		ops.addOption(OptionBuilder.withDescription("keep males only")
				.withLongOpt(cmd_keep_male_long).create(cmd_keep_male));
		ops.addOption(OptionBuilder.withDescription("keep females only")
				.withLongOpt(cmd_keep_female_long).create(cmd_keep_female));
		ops.addOption(OptionBuilder.withDescription("exclude unknown sex")
				.withLongOpt(cmd_ex_nosex_long).create(cmd_ex_nosex));

		// make bed

		ops.addOption(OptionBuilder.withLongOpt(cmd_reference_allele_long)
				.withDescription("set reference allele ").hasArg()
				.create(cmd_reference_allele));

		ops.addOption(OptionBuilder.withLongOpt(cmd_make_bed_long)
				.withDescription("make bed ").create(cmd_make_bed));

		ops.addOption(OptionBuilder.withLongOpt(cmd_order_ind_long)
						.withDescription("order individuals ").hasArg().create(cmd_order_ind));
		ops.addOption(OptionBuilder.withLongOpt(cmd_remove_atgc_long)
						.withDescription("remove atgc loci ").create(cmd_remove_atgc));
		ops.addOption(OptionBuilder.withLongOpt(cmd_zero_geno_long)
						.withDescription("set missing genotypes ").hasArg().create(cmd_zero_geno));

		// grm-stat
		ops.addOption(OptionBuilder.withLongOpt(cmd_grm_stat_long)
				.withDescription("grm statistics").create(cmd_grm_stat));

		// ops.addOption(OptionBuilder.withLongOpt(cmd_exclude_diag_long).withDescription("grm statistics excluded diagonal elements").create(cmd_exclude_diag));

		// haseman-elston regression
		ops.addOption(OptionBuilder.withDescription("h2 ").hasArg()
				.create(cmd_eh2));

		heArgs = new HEArgs();

		ops.addOption(OptionBuilder.withLongOpt(cmd_ref_freq_long)
				.withDescription("reference allele frequency").hasArg()
				.create(cmd_ref_freq));

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_maf_range_long)
				.withDescription(	"only maf withwin this range (inclusive) will be used ")
				.hasArg().create(cmd_maf_range));

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_grm_range_long)
				.withDescription(	"only grm withwin this range (inclusive) will be calculated ")
				.hasArg().create(cmd_grm_range));

		ops.addOption(OptionBuilder.withLongOpt(cmd_grm_partition_long)
				.withDescription("partitioning grm into even subdivisions. ")
				.hasArg().create(cmd_grm_partition));

		ops.addOption(OptionBuilder.withLongOpt(cmd_make_grm_long)
				.withDescription("generate genetic relationship matirx")
				.create(cmd_make_grm));

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_make_grm_txt_long)
				.withDescription(	"generate genetic relationship matirx and save in the plain text format")
				.create(cmd_make_grm_txt));

		ops.addOption(OptionBuilder.withDescription("covariate file").hasArg()
				.create(cmd_covar));

		ops.addOption(OptionBuilder.withLongOpt(cmd_covar_num_long)
				.withDescription("covariate index").hasArg()
				.create(cmd_covar_num));

		ops.addOption(OptionBuilder
				.withDescription("quantitative covariate file").hasArg()
				.create(cmd_qcovar));

		ops.addOption(OptionBuilder.withLongOpt(cmd_qcovar_num_long)
				.withDescription("quantitative covariate index").hasArg()
				.create(cmd_qcovar_num));

		ops.addOption(OptionBuilder.withDescription("reverse ")
				.create(cmd_reverse));

		ops.addOption(OptionBuilder
				.withDescription("standardise the phenotype").create(cmd_scale));

		ops.addOption(OptionBuilder.withDescription("perm ").hasArg()
				.create(cmd_perm));

		ops.addOption(OptionBuilder.withDescription("prevalence ").hasArg()
				.create(cmd_k));

		ops.addOption(OptionBuilder
				.withDescription("Strings for representing \"Not Available\"")
				.hasArg().create("na"));

		hpcArgs = new HpcArgs();

		// /////transform heritability
		ops.addOption(OptionBuilder
				.withLongOpt(cmd_cal_k_long)
				.withDescription(	"calculate heritability on the liability/observed scale with value K " + cmd_cal_k)
				.hasArg().create(cmd_cal_k));

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_cal_hl_long)
				.withDescription(	"calculate heritability on the liability/observed scale " + cmd_cal_hl_long)
				.hasArg().create(cmd_cal_hl));

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_cal_ho_long)
				.withDescription(	"calculate heritability on the liability/observed scale " + cmd_cal_ho_long)
				.hasArg().create(cmd_cal_ho));

		ops.addOption(OptionBuilder.withLongOpt(cmd_cal_cc_long)
				.withDescription("number of case and controls " + cmd_cal_cc)
				.hasArg().create(cmd_cal_cc));

		ops.addOption(OptionBuilder
				.withLongOpt(cmd_cal_h2_se_long)
				.withDescription(	"se of heritability on the liability/observed scale ")
				.hasArg().create(cmd_cal_h2_se));

		ops.addOption(OptionBuilder
				.withDescription("root file, default = " + out).hasArg()
				.create(cmd_out));

		ops.addOption(OptionBuilder.withDescription("help manual.")
				.create(cmd_help));
	}

	public class BFileArgs
	{

		@SuppressWarnings("static-access")
		private BFileArgs(String desc, String opt)
		{
			ops.addOption(OptionBuilder.withDescription(desc).hasArg()
					.create(opt));
			cmd_bfile = opt;
		}

		public boolean isSet()
		{
			return cmdLine.hasOption(cmd_bfile);
		}

		public String getBed()
		{
			String bfile = cmdLine.getOptionValue(cmd_bfile);
			return bfile == null ? null : bfile + ".bed";
		}

		public String getBim()
		{
			String bfile = cmdLine.getOptionValue(cmd_bfile);
			return bfile == null ? null : bfile + ".bim";
		}

		public String getFam()
		{
			String bfile = cmdLine.getOptionValue(cmd_bfile);
			return bfile == null ? null : bfile + ".fam";
		}

		private String cmd_bfile;
	}

	public BFileArgs getBFileArgs(int i)
	{
		return bfileArgs[i];
	}

	private BFileArgs[] bfileArgs;

	public class FileArgs
	{
		@SuppressWarnings("static-access")
		private FileArgs()
		{
			ops.addOption(OptionBuilder
					.withDescription("PLINK format text input file").hasArg()
					.create("file"));
		}

		public boolean isSet()
		{
			return cmdLine.hasOption("file");
		}

		public String getPed()
		{
			String file = cmdLine.getOptionValue("file");
			return file == null ? null : file + ".ped";
		}

		public String getMap()
		{
			String file = cmdLine.getOptionValue("file");
			return file == null ? null : file + ".map";
		}
	}

	public FileArgs getFileArgs()
	{
		return fileArgs;
	}

	private FileArgs fileArgs;

	public RegressionModel getTranFunction()
	{
		return tranFunction;
	}

	private final String cmd_linear = "linear";
	private final String cmd_logit = "logit";
	private RegressionModel tranFunction = RegressionModel.LINEAR;

	// /////////////simulation real data

	private final String cmd_bsimu = "bsimu";
	public boolean bsimuFlag = false;

	private final String cmd_seed = "seed";
	public long simuSeed = 2012;

	private final String cmd_simu_rep = "simu_rep";
	private final String cmd_simu_rep_long = "simu-rep";
	public int simuRep = 1;

	private final String cmd_simu_causal_loci = "simu_causal_loci";
	private final String cmd_simu_causal_loci_long = "simu-causal-loci";
	public String simuCasualLoci = null;

	private final String cmd_simu_rnd_causal_loci = "simu_rnd_causal_loci";
	private final String cmd_simu_rnd_causal_loci_long = "simu-rnd-causal-loci";
	public int simuRndCausalLoci = 0;

	private final String cmd_simu_hsq = "simu_hsq";
	private final String cmd_simu_hsq_long = "simu-hsq";
	public double simuHsq = 0.5;

	private final String cmd_simu_qt = "simu_qt";
	private final String cmd_simu_qt_long = "simu-qt";
	public boolean simupolyQTFlag = false;

	private final String cmd_simu_order = "simu_order";
	private final String cmd_simu_order_long = "simu-order";
	public boolean simuOrderFlag = false;

	private final String cmd_simu_cc = "simu_cc";
	private final String cmd_simu_cc_long = "simu-cc";
	public int[] simuCC = { 0, 0 };
	public boolean simupolyCCFlag = false;

	private final String cmd_simu_k = "simu_k";
	private final String cmd_simu_k_long = "simu-k";
	public double simuK = 0.1;

	public final int sm_qt = 0;
	public final int sm_cc = 1;

	// ///////////////simulation polygenic
	private final String cmd_poly_loci = "poly_loci";
	private final String cmd_poly_loci_long = "poly-loci";

	public int polyLoci = 1000;

	private final String cmd_poly_loci_null = "poly_loci_null";
	private final String cmd_poly_loci_null_long = "poly-loci-null";

	public int polyLociNull = 0;
	public int poly_sample_QT = 1000;

	private final String cmd_poly_LD = "poly_ld";
	private final String cmd_poly_LD_long = "poly-ld";
	public double polyLD = 0;

	private final String cmd_poly_U = "poly_U";
	private final String cmd_poly_U_long = "poly-U";
	public boolean polyU = false;

	private final String cmd_poly_freq = "poly_freq";
	private final String cmd_poly_freq_long = "poly-freq";
	public double polyFreq = 0.5;

	private final String cmd_poly_effect = "poly_effect";
	private final String cmd_poly_effect_long = "poly-effect";
	public boolean polyEffectFlag = false;
	public String polyEffectFile = null;

	//HE linkage
	///////////////////ibd
	private final String cmd_quickibd = "quick_ibd";
	private final String cmd_quickibd_long = "quick-ibd";

	public boolean quickibdFlag = false;
	
	////Joint HE Linkage ls
	private final String cmd_sd = "sd";
	public boolean sdFlag = true;

	private final String cmd_ss = "ss";
	public boolean ssFlag = false;

	private final String cmd_cp = "cp";
	public boolean cpFlag = true;

	
	private final String cmd_helink = "he_link";
	private final String cmd_helink_long = "he-link";
	public boolean helinkFlag = false;
	
	private final String cmd_hejoint = "he_joint";
	private final String cmd_hejoint_long = "he-joint";
	public boolean hejointFlag = false;

	private final String cmd_ibd = "ibd";
	public String ibdFile = null;
	
	private final String cmd_reml = "reml";
	public boolean remlFlag = false;
	
	// /////////////////nontrans
	private final String cmd_nontrans = "nontrans";
	public boolean nontransFlag = false;

	private final String cmd_nontrans_cases = "nontrans_cases";
	private final String cmd_nontrans_cases_long = "nontrans-cases";
	public boolean nontranscasesFlag = false;

	private final String cmd_nontrans_controls = "nontrans_controls";
	private final String cmd_nontrans_controls_long = "nontrans-controls";
	public boolean nontranscontrolsFlag = false;
	
	// /////////////////pop stat
	private final String cmd_freq = "freq";
	public boolean freqFlag = false;
	private final String cmd_geno_freq = "geno_freq";
	private final String cmd_geno_freq_long = "geno-freq";
	public boolean genoFreqFlag = false;

	public final String cmd_sum_stat_help = "sum_stat_help";
	public final String cmd_sum_stat_help_long = "sum-stat-help";
	public final int freq = 0;
	public final int geno_freq = 1;
	public boolean sumStatFlag = false;

	/////////meta watchdog

	private final String cmd_dog_alpha = "dogalpha";
	public double dog_alpha = 0.05;
	
	private final String cmd_watchdog = "watchdog";
	public boolean watchdogFlag = false;

	private final String cmd_set1 = "set1";
	public String set1_file = null;

	private final String cmd_set2 = "set2";
	public String set2_file = null;

	private final String cmd_dog_cutoff = "dogcutoff";
	public double dog_cutoff = 0.05;

	///////IMPUTE to bed
	private final String cmd_impute_format_long = "impute";
	public boolean imputeFlag = false;
	private final String cmd_impute_batch_long = "impute-batch";
	public String imputeBatchFile = null;

	// fst
	private final String cmd_fst = "fst";
	public boolean fstFlag = false;
	public String fst_file = null;

	// grm statistics
	private final String cmd_grm_stat = "grm_stat";
	private final String cmd_grm_stat_long = "grm-stat";
	public boolean grmstatFlag = false;

	// private final String cmd_exclude_diag = "exclude_diag";
	// private final String cmd_exclude_diag_long = "exclude-diag";
	// public boolean exclude_diag = false;

	// HE regression options Begin
	public boolean hasHEOption()
	{
		return heFlag;
	}

	private boolean heFlag = false;

	public class HEArgs
	{
		@SuppressWarnings("static-access")
		private HEArgs()
		{
			ops.addOption(OptionBuilder.withDescription("grm markers").hasArg()
					.create(cmd_grm_M));
			ops.addOption(OptionBuilder.withLongOpt(cmd_grm_cutoff_long)
					.withDescription("grm cut-off").hasArg()
					.create(cmd_grm_cutoff));
			ops.addOption(OptionBuilder.withLongOpt(cmd_abs_grm_cutoff_long)
					.withDescription("grm absolute cut-off").hasArg()
					.create(cmd_abs_grm_cutoff));
			ops.addOption(OptionBuilder.withLongOpt(cmd_grm_txt_long)
					.withDescription("grm text format").hasArg()
					.create(cmd_grm_txt));
			ops.addOption(OptionBuilder.withLongOpt(cmd_grm_txt_list_long)
					.withDescription("grm text list").hasArg()
					.create(cmd_grm_txt_list));
			ops.addOption(OptionBuilder.withLongOpt(cmd_grm_bin_long)
					.withDescription("grm binary format").hasArg()
					.create(cmd_grm_bin));
			ops.addOption(OptionBuilder.withLongOpt(cmd_grm_bin_list_long)
					.withDescription("grm binary list").hasArg()
					.create(cmd_grm_bin_list));
			ops.addOption(OptionBuilder.withDescription("grm ").hasArg()
					.create(cmd_grm));
			ops.addOption(OptionBuilder.withLongOpt(cmd_grm_list_long)
					.withDescription("grm list").hasArg().create(cmd_grm_list));
			ops.addOption(OptionBuilder.withDescription("phenotype file")
					.hasArg().create(cmd_pheno));
			ops.addOption(OptionBuilder
					.withLongOpt(targetTraitLongOpt)
					.withDescription(	"a positive integer indicating the target trait, i.e. the phenotype column, used for analysis; starting from 1, meaning the first trait")
					.hasArg().withArgName("index").create(targetTraitOpt));
			ops.addOption(OptionBuilder
					.withLongOpt(cmd_sd_long)
					.withDescription("phenotype is coded as squared difference")
					.create(cmd_sd));
			ops.addOption(OptionBuilder.withLongOpt(cmd_ss_long)
					.withDescription("phenotype is coded as squared sum")
					.create(cmd_ss));
			ops.addOption(OptionBuilder.withLongOpt(cmd_cp_long)
					.withDescription("phenotype is coded as cross product")
					.create(cmd_cp));
		}

		private void commandListener()
		{
			if (cmdLine.hasOption(cmd_grm))
			{
				StringBuilder sb1 = new StringBuilder(
						cmdLine.getOptionValue(cmd_grm));
				grm_ = sb1.append(".grm.gz").toString();
				StringBuilder sb2 = new StringBuilder(
						cmdLine.getOptionValue(cmd_grm));
				grm_id = sb2.append(".grm.id").toString();
				isGrm_ = true;
				isGrmBinary_ = false;
				isGrmTxt_ = false;
				isGrmList_ = false;
				isGrmBinaryList_ = false;
				isGrmTxtList_ = false;

				isSingleGrm = true;
				isMultiGrm = false;
			}
			else if (cmdLine.hasOption(cmd_grm_list))
			{
				grmList_ = cmdLine.getOptionValue(cmd_grm_list);
				FileUtil.exists(grmList_);
				isGrm_ = false;
				isGrmBinary_ = false;
				isGrmTxt_ = false;
				isGrmList_ = true;
				isGrmBinaryList_ = false;
				isGrmTxtList_ = false;

				isSingleGrm = false;
				isMultiGrm = true;

			}
			else if (cmdLine.hasOption(cmd_grm_bin))
			{
				StringBuilder sb1 = new StringBuilder(
						cmdLine.getOptionValue(cmd_grm_bin));
				grm_ = sb1.append(".grm.bin").toString();
				StringBuilder sb2 = new StringBuilder(
						cmdLine.getOptionValue(cmd_grm_bin));
				grm_id = sb2.append(".grm.id").toString();
				isGrm_ = false;
				isGrmBinary_ = true;
				isGrmTxt_ = false;
				isGrmList_ = false;
				isGrmBinaryList_ = false;
				isGrmTxtList_ = false;

				isSingleGrm = true;
				isMultiGrm = false;
			}
			else if (cmdLine.hasOption(cmd_grm_bin_list))
			{
				grmList_ = cmdLine.getOptionValue(cmd_grm_bin_list);
				FileUtil.exists(grmList_);
				isGrm_ = false;
				isGrmBinary_ = false;
				isGrmTxt_ = false;
				isGrmList_ = false;
				isGrmBinaryList_ = true;
				isGrmTxtList_ = false;

				isSingleGrm = false;
				isMultiGrm = true;
			}
			else if (cmdLine.hasOption(cmd_grm_txt))
			{
				StringBuilder sb1 = new StringBuilder(
						cmdLine.getOptionValue(cmd_grm_txt));
				grm_ = sb1.append(".grm.txt").toString();
				StringBuilder sb2 = new StringBuilder(
						cmdLine.getOptionValue(cmd_grm_txt));
				grm_id = sb2.append(".grm.id").toString();
				isGrm_ = false;
				isGrmBinary_ = false;
				isGrmTxt_ = true;
				isGrmList_ = false;
				isGrmBinaryList_ = false;
				isGrmTxtList_ = false;

				isSingleGrm = true;
				isMultiGrm = false;
			}
			else if (cmdLine.hasOption(cmd_grm_txt_list))
			{
				grmList_ = cmdLine.getOptionValue(cmd_grm_txt_list);
				FileUtil.exists(grmList_);
				isGrm_ = false;
				isGrmBinary_ = false;
				isGrmTxt_ = false;
				isGrmList_ = false;
				isGrmBinaryList_ = false;
				isGrmTxtList_ = true;

				isSingleGrm = false;
				isMultiGrm = true;
			}

			if (cmdLine.hasOption(cmd_grm_M))
			{
				grm_M_ = Integer.parseInt(cmdLine.getOptionValue(cmd_grm_M));
			}

			if (cmdLine.hasOption(cmd_grm_cutoff))
			{
				GrmCutoff_ = Double.parseDouble(cmdLine
						.getOptionValue(cmd_grm_cutoff));
				GrmCutoffFlag_ = true;
				AbsGrmCutoffFlag_ = false;
			}

			if (cmdLine.hasOption(cmd_abs_grm_cutoff))
			{
				AbsGrmCutoff_ = Double.parseDouble(cmdLine
						.getOptionValue(cmd_abs_grm_cutoff));
				AbsGrmCutoffFlag_ = true;
				GrmCutoffFlag_ = false;
			}

			if (cmdLine.hasOption(cmd_pheno))
			{
				pheno = cmdLine.getOptionValue(cmd_pheno);
				FileUtil.exists(pheno);
			}

			if (cmdLine.hasOption(targetTraitOpt))
			{
				boolean valid = true;

				String mphenoOptValStr = cmdLine.getOptionValue(targetTraitOpt);

				try
				{
					targetTraitOptVal = Integer.parseInt(mphenoOptValStr);
				}
				catch (NumberFormatException e)
				{
					valid = false;
				}

				if (!valid || targetTraitOptVal <= 0)
				{
					String msg = "";
					msg += mphenoOptValStr + " is not a valid value of --" + getTargetTraitLongOption() + "/-" + getTargetTraitOption() + " option. ";
					msg += "It should be a positive integer indicating the phenotype column for analysis.";
					Logger.printUserError(msg);
					System.exit(1);
				}
			}

			if (cmdLine.hasOption(cmd_sd))
			{
				type = HEType.SD;
				heFlag = true;
			}
			else if (cmdLine.hasOption(cmd_ss))
			{
				type = HEType.SS;
				heFlag = true;
			}
			else if (cmdLine.hasOption(cmd_cp))
			{
				type = HEType.CP;
				heFlag = true;
			}
		}

		public HEType getType()
		{
			return type;
		}

		public boolean isSingleGrm()
		{
			return isSingleGrm;
		}

		public boolean isMultiGrm()
		{
			return isMultiGrm;
		}

		public boolean isGrm()
		{
			return isGrm_;
		}

		public boolean isGrmList()
		{
			return isGrmList_;
		}

		public boolean isGrmBinary()
		{
			return isGrmBinary_;
		}

		public boolean isGrmBinaryList()
		{
			return isGrmBinaryList_;
		}

		public boolean isGrmTxt()
		{
			return isGrmTxt_;
		}

		public boolean isGrmTxtList()
		{
			return isGrmTxtList_;
		}

		public boolean isGrmCutoff()
		{
			return GrmCutoffFlag_;
		}

		public boolean isAbsGrmCutoff()
		{
			return AbsGrmCutoffFlag_;
		}

		public double GrmCutoff()
		{
			return GrmCutoff_;
		}

		public double AbsGrmCutoff()
		{
			return AbsGrmCutoff_;
		}

		public int getGrmM()
		{
			return grm_M_;
		}

		public String getGrm()
		{
			return grm_;
		}

		public String getGrmId()
		{
			return grm_id;
		}

		public String getGrmList()
		{
			return grmList_;
		}

		public String getPheno()
		{
			return pheno;
		}

		public String getTargetTraitLongOption()
		{
			return targetTraitLongOpt;
		}

		public String getTargetTraitOption()
		{
			return targetTraitOpt;
		}

		public int getTargetTraitOptionValue()
		{
			return targetTraitOptVal;
		}

		private final String cmd_sd = "he_sd"; // (y1-y2)^2
		private final String cmd_sd_long = "he-sd";
		private final String cmd_ss = "he_ss"; // (y1+y2)^2
		private final String cmd_ss_long = "he-ss";
		private final String cmd_cp = "he_cp"; // y1*y2
		private final String cmd_cp_long = "he-cp";

		private HEType type;

		private final String cmd_grm_cutoff = "grm_cutoff";
		private final String cmd_grm_cutoff_long = "grm-cutoff";
		private double GrmCutoff_ = 0;
		private boolean GrmCutoffFlag_ = false;
		private final String cmd_abs_grm_cutoff = "grm_abs_cutoff";
		private final String cmd_abs_grm_cutoff_long = "grm-abs-cutoff";
		private double AbsGrmCutoff_ = 0;
		private boolean AbsGrmCutoffFlag_ = false;
		private final String cmd_grm_M = "marker";

		private final String cmd_grm_txt = "grm_txt";
		private final String cmd_grm_txt_long = "grm-txt";
		private final String cmd_grm_txt_list = "grm_txt_list";
		private final String cmd_grm_txt_list_long = "grm-txt-list";

		private final String cmd_grm_bin = "grm_bin";
		private final String cmd_grm_bin_long = "grm-bin";
		private final String cmd_grm_bin_list = "grm_bin_list";
		private final String cmd_grm_bin_list_long = "grm-bin-list";

		private final String cmd_grm = "grm";
		private final String cmd_grm_list = "grm_list";
		private final String cmd_grm_list_long = "grm-list";

		private boolean isGrm_;
		private boolean isGrmBinary_;
		private boolean isGrmTxt_;
		private boolean isGrmList_;
		private boolean isGrmTxtList_;
		private boolean isGrmBinaryList_;
		private boolean isSingleGrm = false;
		private boolean isMultiGrm = false;
		private String grm_ = null;
		private String grm_id = null;
		private String grmList_ = null;

		private int grm_M_ = -1;
		private final String cmd_pheno = "pheno";
		private String pheno = null;

		private static final String targetTraitLongOpt = "target-trait";
		private static final String targetTraitOpt = "tt";
		private int targetTraitOptVal = 1;

	}

	public HEArgs getHEArgs()
	{
		return heArgs;
	}

	private HEArgs heArgs;
	// HE regression options End

	// make grm options start

	private final String cmd_ref_freq = "ref_freq";
	private final String cmd_ref_freq_long = "ref-freq";
	public String ref_freq = null;

	public boolean GRMFlag = false;

	private final String cmd_make_grm = "make_grm";
	private String cmd_make_grm_long = "make-grm";
	public boolean makeGRMFlag = false;

	private final String cmd_make_grm_txt = "make_grm_txt";
	private String cmd_make_grm_txt_long = "make-grm-txt";
	public boolean makeGRMTXTFlag = false;

	private final String cmd_maf_range = "maf_range";
	private final String cmd_maf_range_long = "maf-range";
	public double[] maf_range = { 0, 1 };

	private final String cmd_grm_range = "grm_range";
	private final String cmd_grm_range_long = "grm-range";
	public int[] grm_range = { 0, 1 };
	public boolean grmRangeFlag = false;

	private final String cmd_grm_partition = "grm_partition";
	private final String cmd_grm_partition_long = "grm-partition";
	public int grmPartition = 0;
	public boolean grmPartitionFlag = false;

	// make grm options end

	// quantitative covariates
	private final String cmd_qcovar = "qcovar";
	public String qcovar_file = null;
	private final String cmd_qcovar_num = "qcovar_num";
	private final String cmd_qcovar_num_long = "qcovar-num";
	public int[] qcovar_num = null;

	// categorical covariates
	private final String cmd_covar = "covar";
	public String covar_file = null;
	private final String cmd_covar_num = "covar_num";
	private final String cmd_covar_num_long = "covar-num";
	public int[] covar_num = null;

	private final String cmd_reverse = "reverse";
	public boolean reverse = false;

	private final String cmd_k = "k";
	public boolean k_button = false;
	public double k = 0.01;

	private final String cmd_scale = "scale";
	public boolean scale = false;

	public double eh2 = 1;
	private final String cmd_eh2 = "eh2";
	public boolean eh2Flag = false;

	private final String cmd_out = "out";
	public String out = "gear";

	private final String cmd_perm = "perm";
	public int perm = 100;
	public boolean permFlag = false;

	// /////////////////heritability transformation
	public boolean calOption = false;
	private final String cmd_cal_k = "cal_k";
	private final String cmd_cal_k_long = "cal-k";
	public double cal_k = 0;

	private final String cmd_cal_hl = "cal_hl";
	private final String cmd_cal_hl_long = "cal-hl";
	public boolean cal_hlFlag = false;
	public double cal_hl = 0;

	private final String cmd_cal_ho = "cal_ho";
	private final String cmd_cal_ho_long = "cal-ho";
	public boolean cal_hoFlag = false;
	public double cal_ho = 0;

	private final String cmd_cal_h2_se = "cal_h2_se";
	private final String cmd_cal_h2_se_long = "cal-h2-se";
	public boolean cal_h2seFlag = false;
	public double cal_h2_se = -1;

	private final String cmd_cal_cc = "cal_cc";
	private final String cmd_cal_cc_long = "cal-cc";
	public double[] cal_cc = { 0, 0 };

	public class HpcArgs
	{
		@SuppressWarnings("static-access")
		private HpcArgs()
		{
			ops.addOption(OptionBuilder.withDescription("email").hasArg()
					.create("email"));
			ops.addOption(OptionBuilder.withDescription("ram").hasArg()
					.create("ram"));
			ops.addOption(OptionBuilder.withDescription("name").hasArg()
					.create("name"));
		}

		public String getEmail()
		{
			return cmdLine.getOptionValue("email", "guobo.chen@uq.edu.au");
		}

		public String getRam()
		{
			return cmdLine.getOptionValue("ram", "10G");
		}

		public String getName()
		{
			return cmdLine.getOptionValue("name", "gear");
		}
	}

	public HpcArgs getHpcArgs()
	{
		return hpcArgs;
	}

	private HpcArgs hpcArgs;

	// /naive imputation
	private final String cmd_naive_imputation = "naive_imputation";
	private final String cmd_naive_imputation_long = "naive-imputation";
	public boolean naiveImputFlag = false;

	// /////////////////level 1 snp selection
	private final String cmd_chr = "chr";
	public String[] inchr = null;
	public String[] exchr = null;
	public boolean inchrFlag = false;
	public boolean exchrFlag = false;

	private final String cmd_snps = "snps";
	public String snpList = null;

	// ////////////////level 1 individual selection
	private final String cmd_keep = "keep";
	public String keepFile = null;
	public boolean keepFlag = false;

	// /////////////// individual selection start
	private final String cmd_remove = "remove";
	public String removeFile = null;
	public boolean removeFlag = false;

	// /////////////// reference-allele
	private final String cmd_reference_allele = "reference_allele";
	private final String cmd_reference_allele_long = "refernce-allele";
	public String reference_allele = null;

	// ///////////////write bed file

	private final String cmd_make_bed = "make_bed";
	private final String cmd_make_bed_long = "make-bed";
	public boolean makebedFlag = false;

	private final String cmd_order_ind = "order_ind";
	private final String cmd_order_ind_long = "order-ind";
	public String orderindFile = null;
	public boolean orderindFlag = false;

	private final String cmd_remove_atgc = "remove_atgc";
	private final String cmd_remove_atgc_long = "remove-atgc";
	public boolean removeatgcFlag = false;

	private final String cmd_zero_geno = "zero_geno";
	private final String cmd_zero_geno_long = "zero-geno";
	public double zerogeno = -1;
	public boolean zerogenoFlag = false;

	/*
	 * private final String cmd_ex_ind = "exind"; public String[][] ex_ind =
	 * null; private final String cmd_ex_ind_file = "exindfile"; public boolean
	 * exindFlag = false;
	 */
	private final String cmd_keep_male = "male";
	private final String cmd_keep_male_long = "keep-male";
	public boolean keep_maleFlag = false;

	private final String cmd_keep_female = "female";
	private final String cmd_keep_female_long = "keep-female";
	public boolean keep_femaleFlag = false;

	private final String cmd_ex_nosex = "exnosex";
	private final String cmd_ex_nosex_long = "exclude-nosex";
	public boolean ex_nosexFlag = false;
	// /////////////////global

	public boolean status_shiftFlag = false;

	public double status_shift = -1;

	public String missingGenotype = "22";

	public boolean covar_header_flag = false;

	public boolean genoFlag = false;

	public double geno = 0;

	public boolean mafFlag = false;

	public double maf = 0;

	public boolean maxmafFlag = false;

	public double max_maf = 0.55;

	private final String cmd_help = "help";

	public void parse(String[] args)
	{
		try
		{
			cmdLine = parser.parse(ops, args);
		}
		catch (ParseException e)
		{
			Logger.printUserError(e.getMessage());
			System.exit(1);
		}

		if (args.length == 0 || cmdLine.hasOption(cmd_help))
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("HE Regression", ops);
			System.exit(0);
		}

		if (cmdLine.hasOption(cmd_linear))
		{
			tranFunction = RegressionModel.LINEAR;
		}

		if (cmdLine.hasOption(cmd_logit))
		{
			tranFunction = RegressionModel.LOGIT;
		}

		// naive imputation
		if (cmdLine.hasOption(cmd_naive_imputation))
		{
			naiveImputFlag = true;
		}

		// snp selection
		if (cmdLine.hasOption(cmd_chr))
		{

			String[] chr = cmdLine.getOptionValues(cmd_chr);
			HashSet<String> chrSet = NewIt.newHashSet();
			HashSet<String> exSet = NewIt.newHashSet();
			for (int i = 0; i < chr.length; i++)
			{
				if (chr[i].startsWith("-"))
				{
					exSet.add(chr[i].substring(1, chr[i].length()));
				}
				else
				{
					chrSet.add(chr[i]);
				}
			}
			if (chr.length != chrSet.size() + exSet.size())
			{
				Logger.printUserError("Bad parameter for optin --" + cmd_chr + ".");
				System.exit(1);
			}
			if (chrSet.size() > 0)
			{
				chr = chrSet.toArray(new String[0]);
				inchrFlag = true;
			}
			if (exSet.size() > 0)
			{
				exchr = exSet.toArray(new String[0]);
				exchrFlag = true;
			}
		}

		if (cmdLine.hasOption(cmd_snps))
		{
			snpList = cmdLine.getOptionValue(cmd_snps);
			FileUtil.exists(snpList);
		}

		// individual selection 1 keep
		if (cmdLine.hasOption(cmd_keep))
		{
			keepFile = cmdLine.getOptionValue(cmd_keep);
			FileUtil.exists(keepFile);
			keepFlag = true;
		}

		if (cmdLine.hasOption(cmd_keep_male))
		{
			keep_maleFlag = true;
		}
		if (cmdLine.hasOption(cmd_keep_female))
		{
			keep_femaleFlag = true;
		}
		if (cmdLine.hasOption(cmd_ex_nosex))
		{
			ex_nosexFlag = true;
		}
		if (cmdLine.hasOption(cmd_remove))
		{
			removeFile = cmdLine.getOptionValue(cmd_remove);
			FileUtil.exists(removeFile);
			removeFlag = true;
		}

		// ///// set reference
		if (cmdLine.hasOption(cmd_reference_allele))
		{
			reference_allele = cmdLine.getOptionValue(cmd_reference_allele);
			FileUtil.exists(reference_allele);
		}

		// make bed
		if (cmdLine.hasOption(cmd_make_bed))
		{
			makebedFlag = true;
		}

		if (cmdLine.hasOption(cmd_order_ind))
		{
			orderindFile = cmdLine.getOptionValue(cmd_order_ind);
			FileUtil.exists(orderindFile);
			orderindFlag = true;
		}

		if (cmdLine.hasOption(cmd_remove_atgc))
		{
			removeatgcFlag = true;
		}
		
		if (cmdLine.hasOption(cmd_zero_geno))
		{
			zerogeno = Double.parseDouble(cmdLine.getOptionValue(cmd_zero_geno));
			zerogenoFlag = true;
			if (zerogeno < 0 || zerogeno >1)
			{
				Logger.printUserLog("missing rate for genotype should be between 0 and 1.");
				System.exit(1);
			}
		}

		// ibd
		if (cmdLine.hasOption(cmd_quickibd))
		{
			quickibdFlag = true;
		}

		if (cmdLine.hasOption(cmd_helink))
		{
			helinkFlag = true;
		}

		if (cmdLine.hasOption(cmd_sd))
		{
			sdFlag = true;
			ssFlag = false;
			cpFlag = false;
		}

		if (cmdLine.hasOption(cmd_ss))
		{
			sdFlag = false;
			ssFlag = true;
			cpFlag = false;
		}

		if (cmdLine.hasOption(cmd_cp))
		{
			sdFlag = false;
			ssFlag = false;
			cpFlag = true;
		}

		if (cmdLine.hasOption(cmd_hejoint))
		{
			hejointFlag = true;
		}

		if (cmdLine.hasOption(cmd_reml))
		{
			remlFlag = true;
		}

		if (cmdLine.hasOption(cmd_ibd))
		{
			ibdFile = cmdLine.getOptionValue(cmd_ibd);
			FileUtil.exists(ibdFile);
		}

		// nontrans
		if (cmdLine.hasOption(cmd_nontrans))
		{
			nontransFlag = true;
		}

		if (cmdLine.hasOption(cmd_nontrans_cases))
		{
			nontranscasesFlag = true;
			nontranscontrolsFlag = false;
		}

		if (cmdLine.hasOption(cmd_nontrans_controls))
		{
			nontranscontrolsFlag = true;
			nontranscasesFlag = false;
		}

		// pop stat
		if (cmdLine.hasOption(cmd_freq))
		{
			sumStatFlag = true;
			freqFlag = true;
		}
		if (cmdLine.hasOption(cmd_geno_freq))
		{
			sumStatFlag = true;
			genoFreqFlag = true;
		}
		if (cmdLine.hasOption(cmd_fst))
		{
			sumStatFlag = true;
			fstFlag = true;
			fst_file = cmdLine.getOptionValue(cmd_fst);
			FileUtil.exists(fst_file);
		}

		if (cmdLine.hasOption(cmd_dog_alpha))
		{
			dog_alpha = Double.parseDouble(cmdLine.getOptionValue(cmd_dog_alpha));
			if(dog_alpha < 0 || dog_alpha >= 1.0)
			{
				Logger.printUserLog("dog alpha should be between 0 and 1.");
				System.exit(1);
			}
		}

		if (cmdLine.hasOption(cmd_watchdog))
		{
			watchdogFlag = true;
		}

		if (cmdLine.hasOption(cmd_set1))
		{
			set1_file = cmdLine.getOptionValue(cmd_set1);
			FileUtil.exists(set1_file);
		}

		if (cmdLine.hasOption(cmd_set2))
		{
			set2_file = cmdLine.getOptionValue(cmd_set2);
			FileUtil.exists(set2_file);
		}

		if (cmdLine.hasOption(cmd_dog_cutoff))
		{
			dog_cutoff = Double.parseDouble(cmdLine.getOptionValue(cmd_dog_cutoff));
			if(dog_cutoff < 0 || dog_cutoff >= 1.0)
			{
				Logger.printUserLog("alpha should be between 0 and 1.");
				System.exit(0);
			}
		}

		//impute
		if (cmdLine.hasOption(cmd_impute_format_long))
		{
			imputeFlag = true;
		}

		if (cmdLine.hasOption(cmd_impute_batch_long))
		{
			imputeBatchFile = cmdLine.getOptionValue(cmd_impute_batch_long);
			FileUtil.exists(imputeBatchFile);
		}

		// simulation real data
		if (cmdLine.hasOption(cmd_bsimu))
		{
			bsimuFlag = true;
		}

		if (cmdLine.hasOption(cmd_simu_qt))
		{
			simupolyQTFlag = true;

			poly_sample_QT = Integer.parseInt(cmdLine
					.getOptionValue(cmd_simu_qt));
		}

		if (cmdLine.hasOption(cmd_simu_cc))
		{

			String[] s = cmdLine.getOptionValue(cmd_simu_cc).split(",");
			simuCC[0] = Integer.parseInt(s[0]);
			simuCC[1] = Integer.parseInt(s[1]);
			simupolyCCFlag = true;
		}

		if (cmdLine.hasOption(cmd_simu_order))
		{
			simuOrderFlag = true;
		}

		if (cmdLine.hasOption(cmd_seed))
		{
			simuSeed = Long.parseLong(cmdLine.getOptionValue(cmd_seed));
		}

		if (cmdLine.hasOption(cmd_simu_hsq))
		{
			simuHsq = Double.parseDouble(cmdLine.getOptionValue(cmd_simu_hsq));
			if (simuHsq < 0 || simuHsq > 1)
			{
				Logger.printUserError("Simulation heritability should be between 0 and 1 (exclusively).");
				System.exit(1);
			}
		}

		if (cmdLine.hasOption(cmd_simu_causal_loci))
		{
			simuCasualLoci = cmdLine.getOptionValue(cmd_simu_causal_loci);
		}

		if (cmdLine.hasOption(cmd_simu_rnd_causal_loci))
		{
			simuRndCausalLoci = Integer.parseInt(cmdLine
					.getOptionValue(cmd_simu_rnd_causal_loci));
		}

		if (cmdLine.hasOption(cmd_simu_k))
		{
			simuK = Double.parseDouble(cmdLine.getOptionValue(cmd_simu_k));
			if (simuK < 0 || simuK > 1)
			{
				Logger.printUserError("Simulation prevalence should be between 0 and 1 (inclusively)");
				System.exit(1);
			}
		}

		if (cmdLine.hasOption(cmd_simu_rep))
		{
			simuRep = Integer.parseInt(cmdLine.getOptionValue(cmd_simu_rep));
			if (simuRep < 0)
			{
				Logger.printUserError("simulation replication should be no smaller than zero");
				System.exit(1);
			}
		}

		// simulation polygenic

		if (cmdLine.hasOption(cmd_poly_loci))
		{
			polyLoci = Integer.parseInt(cmdLine.getOptionValue(cmd_poly_loci));
		}

		if (cmdLine.hasOption(cmd_poly_loci_null))
		{
			polyLociNull = Integer.parseInt(cmdLine
					.getOptionValue(cmd_poly_loci_null));
		}

		if (cmdLine.hasOption(cmd_poly_LD))
		{
			polyLD = Double.parseDouble(cmdLine.getOptionValue(cmd_poly_LD));
		}

		if (cmdLine.hasOption(cmd_poly_U))
		{
			polyU = true;
		}

		if (cmdLine.hasOption(cmd_poly_freq))
		{
			polyFreq = Double
					.parseDouble(cmdLine.getOptionValue(cmd_poly_freq));
		}

		if (cmdLine.hasOption(cmd_poly_effect))
		{
			polyEffectFlag = true;
			polyEffectFile = cmdLine.getOptionValue(cmd_poly_effect);
			FileUtil.exists(polyEffectFile);
		}

		// grm statistics
		if (cmdLine.hasOption(cmd_grm_stat))
		{
			grmstatFlag = true;
		}

		// if (cl.hasOption(cmd_exclude_diag)) {
		// exclude_diag = true;
		// }

		// haseman-elston regression

		if (cmdLine.hasOption(cmd_eh2))
		{
			eh2Flag = true;
			eh2 = Double.parseDouble(cmdLine.getOptionValue(cmd_eh2));
		}

		heArgs.commandListener();

		if (cmdLine.hasOption(cmd_ref_freq))
		{
			ref_freq = cmdLine.getOptionValue(cmd_ref_freq);
			FileUtil.exists(ref_freq);
		}

		if (cmdLine.hasOption(cmd_maf_range))
		{
			String s = cmdLine.getOptionValue(cmd_maf_range);
			String[] ss = s.split(",");
			maf_range[0] = Double.parseDouble(ss[0]);
			maf_range[1] = Double.parseDouble(ss[1]);
		}

		if (cmdLine.hasOption(cmd_grm_range))
		{
			String s = cmdLine.getOptionValue(cmd_grm_range);
			String[] ss = s.split(",");
			grm_range[0] = Integer.parseInt(ss[0]);
			grm_range[1] = Integer.parseInt(ss[1]);
			grmRangeFlag = true;
		}

		if (cmdLine.hasOption(cmd_grm_partition))
		{
			grmPartitionFlag = true;
			grmPartition = Integer.parseInt(cmdLine
					.getOptionValue(cmd_grm_partition));
		}

		if (cmdLine.hasOption(cmd_make_grm))
		{
			makeGRMFlag = true;
			GRMFlag = true;
		}

		if (cmdLine.hasOption(cmd_make_grm_txt))
		{
			makeGRMTXTFlag = true;
			GRMFlag = true;
		}

		if (cmdLine.hasOption(cmd_covar))
		{
			covar_file = cmdLine.getOptionValue(cmd_covar);
			FileUtil.exists(covar_file);
		}

		if (cmdLine.hasOption(cmd_covar_num))
		{
			String[] p = cmdLine.getOptionValue(cmd_covar_num).split(",");
			HashSet<Integer> idx = NewIt.newHashSet();
			for (int i = 0, len = p.length; i < len; i++)
			{
				if (p[i].contains("-"))
				{
					String[] pp = p[i].split("-");
					if (pp.length != 2)
					{
						Logger.printUserError("Bad parameter for option --" + cmd_covar_num_long + ": " + p[i] + ".");
						System.exit(1);
					}
					for (int j = Integer.parseInt(pp[0]); j <= Integer
							.parseInt(pp[1]); j++)
					{
						idx.add(new Integer(j));
					}
				}
				else
				{
					idx.add(new Integer(Integer.parseInt(p[i])));
				}
			}
			covar_num = new int[idx.size()];
			int c = 0;
			for (Iterator<Integer> e = idx.iterator(); e.hasNext();)
			{
				covar_num[c] = e.next().intValue();
				if (covar_num[c] < 0)
				{
					Logger.printUserError("Bad parameter for option --" + cmd_covar_num_long + ": " + covar_num[c] + ".");
					System.exit(1);
				}
				c++;
			}
		}

		if (cmdLine.hasOption(cmd_qcovar))
		{
			qcovar_file = cmdLine.getOptionValue(cmd_qcovar);
			FileUtil.exists(qcovar_file);
		}

		if (cmdLine.hasOption(cmd_qcovar_num))
		{
			String[] p = cmdLine.getOptionValue(cmd_qcovar_num).split(",");
			HashSet<Integer> idx = NewIt.newHashSet();
			for (int i = 0, len = p.length; i < len; i++)
			{
				if (p[i].contains("-"))
				{
					String[] pp = p[i].split("-");
					if (pp.length != 2)
					{
						Logger.printUserError("Bad parameter for option --" + cmd_qcovar_num_long + ": " + p[i] + ".");
						System.exit(1);
					}
					for (int j = Integer.parseInt(pp[0]); j <= Integer
							.parseInt(pp[1]); j++)
					{
						idx.add(new Integer(j));
					}
				}
				else
				{
					idx.add(new Integer(Integer.parseInt(p[i])));
				}
			}
			qcovar_num = new int[idx.size()];
			int c = 0;
			for (Iterator<Integer> e = idx.iterator(); e.hasNext();)
			{
				qcovar_num[c] = e.next().intValue();
				if (qcovar_num[c] < 0)
				{
					Logger.printUserError("Bad parameter for option --" + cmd_qcovar_num_long + ": " + qcovar_num[c] + ".");
					System.exit(1);
				}
				c++;
			}
		}

		if (cmdLine.hasOption(cmd_reverse))
		{
			reverse = true;
		}

		if (cmdLine.hasOption(cmd_scale))
		{
			scale = true;
		}

		if (cmdLine.hasOption(cmd_perm))
		{
			permFlag = true;
			perm = Integer.parseInt(cmdLine.getOptionValue(cmd_perm));
		}

		if (cmdLine.hasOption(cmd_k))
		{
			k_button = true;
			k = Double.parseDouble(cmdLine.getOptionValue(cmd_k));
		}

		if (cmdLine.hasOption(cmd_out))
		{
			out = cmdLine.getOptionValue(cmd_out);
		}

		if (cmdLine.hasOption(cmd_cal_k))
		{
			calOption = true;
			cal_k = Double.parseDouble(cmdLine.getOptionValue(cmd_cal_k));
		}

		if (cmdLine.hasOption(cmd_cal_ho))
		{
			cal_ho = Double.parseDouble(cmdLine.getOptionValue(cmd_cal_ho));
			cal_hoFlag = true;
			cal_hlFlag = false;
		}

		if (cmdLine.hasOption(cmd_cal_hl))
		{
			cal_hl = Double.parseDouble(cmdLine.getOptionValue(cmd_cal_hl));
			cal_hoFlag = false;
			cal_hlFlag = true;
		}

		if (cmdLine.hasOption(cmd_cal_cc))
		{
			String[] s = cmdLine.getOptionValue(cmd_cal_cc).split(",");
			cal_cc[0] = Double.parseDouble(s[0]);
			cal_cc[1] = Double.parseDouble(s[1]);
		}

		if (cmdLine.hasOption(cmd_cal_h2_se))
		{
			cal_h2_se = Double.parseDouble(cmdLine
					.getOptionValue(cmd_cal_h2_se));
			cal_h2seFlag = true;
		}
	}

	public Options getOptions()
	{
		return ops;
	}

	public String getNA()
	{
		return cmdLine == null ? null : cmdLine.getOptionValue("na");
	}

	public void printOptionsInEffect()
	{
		Logger.printUserLog("Options in effect: ");

		@SuppressWarnings("rawtypes")
		Iterator optIter = cmdLine.iterator();

		while (optIter.hasNext())
		{
			Option opt = (Option) optIter.next();
			String line = opt.getOpt() == null ? "\t--" + opt.getLongOpt()
					: "\t-" + opt.getOpt();
			String[] argValues = opt.getValues();
			if (argValues != null)
			{
				for (String value : argValues)
				{
					line += " " + value;
				}
			}
			Logger.printUserLog(line);
		}

		Logger.printUserLog("");
	}
}
