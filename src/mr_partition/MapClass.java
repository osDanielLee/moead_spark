package mr_partition;

import java.io.IOException;

import moead.MOEAD;
import mop.MopData;
import mop.MopDataPop;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

import mop.AMOP;
import mop.CMOP;

import problems.AProblem;
import problems.DTLZ1;

import utilities.WrongRemindException;
import utilities.StringJoin;

public class MapClass extends MapReduceBase implements Mapper<Object, Text, Text, Text> {

	private static int innerLoop = 1;
	

	Text weightVector = new Text();
	Text indivInfo = new Text();

	public static void setInnerLoop(int innerLoopTime){
		innerLoop = innerLoopTime;
	}
	
	public void map(Object key, Text value, OutputCollector<Text, Text> output, Reporter reporter) 
			throws IOException{
		String paragraph = value.toString();
		System.out.println("paragraph is \n" + paragraph);
		int popSize = 351;
		int neighbourSize = 20;
		AProblem problem = DTLZ1.getInstance();
		AMOP mop = CMOP.getInstance(popSize, neighbourSize, problem);
		MopDataPop mopData = new MopDataPop(mop);
		System.out.println("map begin ... ");
		mopData.clear();
		try {
			mopData.line2mop(paragraph);
			MOEAD.moead(mopData.mop,innerLoop);
			
			weightVector.set("111111111");
			indivInfo.set(mopData.idealPoint2Line() + "#" +  StringJoin.join(",",mopData.mop.partitionArr));
			output.collect(weightVector, indivInfo);
			System.out.println("output collect ~!~");
			for (int i = 0; i < mopData.mop.chromosomes.size(); i++) {
				weightVector.set(mopData.weight2Line(i));
				indivInfo.set(mopData.mop2Line(i));
				output.collect(weightVector, indivInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
