package plugin;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import models.Client;
import models.KeyPair;
import models.TrainedModel;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import scala.Tuple2;
import utils.LabeledPointBuilder;
import utils.SparkSupport;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 
 * @author zxy
 * @since Sep 20, 2013 11:48:14 PM
 */

public class DataInitPlugin extends PlayPlugin {

	@Override
	public void afterApplicationStart() {
		Logger.info("Starting DataInitPlugin...");
		initData();
		trainDefaultModelIfAbsent();
		Logger.info("DataInitPlugin started.");
	}

	public static void initData() {
		String clientId = "53ea055e0cf2921b57a24e2c";
		String clientName = "河南领军易创";
		KeyPair pair = KeyPair.getCollection(KeyPair.class).findOne("{clientId:#}", clientId).as(KeyPair.class);
		if(pair ==null) {
			Logger.info("------init keypair-----------" );
			KeyPair keyPair = new KeyPair();

			keyPair.setClientId(clientId);
			keyPair.setClientName(clientName);
			keyPair.setAccessKey("1oKIHohJJtA/6NxWHesCHOw==");
			Instant createDate = Instant.parse("2016-04-22T00:00:00.00Z");
			Instant expireDate = createDate.plus(365L, ChronoUnit.DAYS);
			keyPair.setCreateDate(Date.from(createDate));
			keyPair.setEnabled(true);
			keyPair.setExpireDate(Date.from(expireDate));
			keyPair.setSecretKey("146bcbd5448a9bc31a88ebd1da4037f11268912c5");
			keyPair.save();
			Logger.info("------init client-----------" );
			Client client = new Client();
			client.setClientId(clientId);
			client.setClientName(clientName);
			List<KeyPair> keyPairs = new ArrayList<>();
			keyPairs.add(keyPair);
			client.setKeyPairs(keyPairs);
			client.save();
		}
		Logger.info("------init data finished-----------" );
	}
//
//	public static void main(String[] args) {
//		Play.readConfiguration();
//		Play.mode = Play.Mode.DEV;
//		TrainedModel.DEFAULT_MODEL_PATH = Play.configuration.getProperty("ml.resources", "resources/") + "random_forest";
//		trainDefaultModelIfAbsent();
//	}
	/**
	 * 如果没有模型模型，则训练生成默认模型
	 *
	 *
	 * */
	public static void trainDefaultModelIfAbsent()  {
		//todo: use NAS or HDFS or GridFS as default model's storage backend
		if (existDefaultModel()) {
			Logger.info("The default model exist.");
        } else {
            Logger.info("loading sample data...");
            JavaPairRDD<String ,LabeledPoint> sampleData= loadDefaultSampleData();
            Logger.info("training default model...");
            RandomForestModel model = TrainJob.trainRandomForestModel(sampleData, TrainedModel.DEFAULT_MODEL_PATH);
            Logger.info("testing default model...");
            Logger.info(model.toDebugString());
            TrainedModel.TestResult testResult = TrainJob.testRandomForestModel(sampleData.sample(false,0.4), model);
            Logger.info("Default RandomForestModel test result:%s ",testResult.toPrettyJson());
        }
	}
	@Nonnull
	public static JavaPairRDD<String, LabeledPoint> loadDefaultSampleData() {
		List<Tuple2<String, LabeledPoint>> tuple2List = new ArrayList<>();
		try {
			String traingDataPath = Play.configuration.getProperty("ml.resources", "resources/") + "sample_data.txt";

			 tuple2List = StreamSupport.stream(Files.readLines(new File(traingDataPath), Charsets.UTF_8).spliterator(), false).
					skip(1).map(line -> {
				String[] split = line.split("\t");
				String name = split[0];
				int capital = Integer.parseInt(split[1]);
				double industry = LabeledPointBuilder.Nominal(split[2]);
				int regMonths = Integer.parseInt(split[3]);
				int label = Integer.parseInt(split[4]);
				LabeledPoint labeledPoint = new LabeledPoint(label, new DenseVector(new double[]{capital, industry, regMonths}));
				return new Tuple2<>(name, labeledPoint);
			}).collect(Collectors.toList());

		}catch (IOException e){
			 Logger.error(e,"error");
		}
		return SparkSupport.getOrCreateSparkContext().parallelizePairs(tuple2List);
	}

	private static boolean existDefaultModel() {
		return new File(TrainedModel.DEFAULT_MODEL_PATH).exists();
	}


}
