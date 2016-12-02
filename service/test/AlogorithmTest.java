import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.clustering.DistributedLDAModel;
import org.apache.spark.mllib.clustering.LDA;
import org.apache.spark.mllib.clustering.LocalLDAModel;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.feature.IDF;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.GradientBoostedTrees;
import org.apache.spark.mllib.tree.configuration.BoostingStrategy;
import org.apache.spark.mllib.tree.model.GradientBoostedTreesModel;
import org.junit.Test;
import play.test.UnitTest;
import scala.Tuple2;
import utils.SparkSupport;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/5/5 17:25
 */
public class AlogorithmTest extends UnitTest{
    @Test
    public void GBTTree(){
        JavaRDD<LabeledPoint> data = SparkSupport.getOrCreateSparkContext().textFile("resources/smaple_data_nominal.csv").map(line -> {
                    String[] split = line.split(",");
            return new LabeledPoint(Double.parseDouble(split[0]), new DenseVector(new double[]{Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3])}));
                });
        data.cache();
        // Split the data into training and test sets (30% held out for testing)
        JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.7, 0.3});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

// Train a GradientBoostedTrees model.
// The defaultParams for Classification use LogLoss by default.
        BoostingStrategy boostingStrategy = BoostingStrategy.defaultParams("Classification");
        boostingStrategy.setNumIterations(10); // Note: Use more iterations in practice.
        boostingStrategy.getTreeStrategy().setNumClasses(2);
        boostingStrategy.getTreeStrategy().setMaxDepth(5);
// Empty categoricalFeaturesInfo indicates all features are continuous.
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<Integer, Integer>();
        boostingStrategy.treeStrategy().setCategoricalFeaturesInfo(categoricalFeaturesInfo);

        final GradientBoostedTreesModel model =
                GradientBoostedTrees.train(trainingData, boostingStrategy);

// Evaluate model on test instances and compute test error
        JavaPairRDD<Double, Double> predictionAndLabel =
                testData.mapToPair(new PairFunction<LabeledPoint, Double, Double>() {
                    @Override
                    public Tuple2<Double, Double> call(LabeledPoint p) {
                        return new Tuple2<Double, Double>(model.predict(p.features()), p.label());
                    }
                });
        Double testErr =
                1.0 * predictionAndLabel.filter(new Function<Tuple2<Double, Double>, Boolean>() {
                    @Override
                    public Boolean call(Tuple2<Double, Double> pl) {
                        return !pl._1().equals(pl._2());
                    }
                }).count() / testData.count();
        System.out.println("Test Error: " + testErr);
        System.out.println("Learned classification GBT model:\n" + model.toDebugString());

// Save and load model

    }
    static class ReverseComparator
            implements Comparator<Vector>, Serializable {

        private static final long serialVersionUID = 7207038068494060241L;

        static final ReverseComparator REVERSE_ORDER
                = new ReverseComparator();


        public int compare(Vector c1, Vector c2) {

            return Double.compare(c2.apply(c2.argmax()),c1.apply(c1.argmax()));
        }

        private Object readResolve() { return ReverseComparator.REVERSE_ORDER; }

//        @Override
        //public Comparator<Comparable<Tuple2<String ,Double>>> reversed() {
//            return Comparator.naturalOrder();
//        }
    }
     @Test
    public void TFIDF(){
         JavaRDD<List<String>> documents = getDocuments();
         HashMap<Integer,String> index=new HashMap<>();

         HashingTF hashingTF = new HashingTF();
        JavaRDD<Vector> tf  = hashingTF.transform(documents);
         documents.collect().forEach(strings -> strings.forEach(s -> index.putIfAbsent(hashingTF.indexOf(s),s)));
        tf.cache()   ;
        tf.take(50).forEach(vector -> System.out.println(vector.toString()));
        IDFModel idf = new IDF(2).fit(tf);
        JavaRDD<Vector> transform = idf.transform(tf);
        transform.take(50).forEach(vector -> System.err.printf("%s %s ,%s\n",vector.argmax(),vector.apply(vector.argmax()),vector.toJson()));
        transform.takeOrdered(10, ReverseComparator.REVERSE_ORDER).forEach(vector -> System.err.printf("%s %s %s ,%s\n",index.get(vector.argmax()),vector.argmax(),vector.apply(vector.argmax()),vector.toJson()));;



     }



    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .set("spark.scheduler.mode", "FAIR")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.cleaner.ttl", "86400")
                .setIfMissing("spark.master", "local[1]")
                .setAppName("default");
        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(sparkConf));
        String[] stopWords={"与","及","等","以上范围","行政法规及规章规定须审批项目除外","国家法律","上述范围凡涉及国家法律","国家法律法规规定禁止的项目及应经审批方可经营的项目除外","以上范围凡需审批的","凭有效许可证经营","不得经营","以上凭资质证经营","未获得审批前不得经营","凭有效许可证在核定的范围内经营","凭有效许可证核定范围经营","国家法律法规禁止或者应经审批的项目除外"};
//        RegexTokenizer regexTokenizer = new RegexTokenizer().setInputCol("text").setOutputCol("tokens").setPattern("\\W+");
//        CountVectorizerModel countVectorizerModel = new CountVectorizerModel(stopWords).setInputCol(regexTokenizer.getOutputCol()).setOutputCol("vector");
//        Pipeline pipeline = new Pipeline().setStages(new PipelineStage[]{regexTokenizer, countVectorizerModel});

        JavaRDD<List<String>> documents = jsc.textFile("resources/sample_opscope.txt").map(line -> {
            line = line.replaceAll("\\(\\S+\\)","").replaceAll(String .format("(%s)",String.join("|",stopWords))," ");
            List<String> strings = Arrays.asList(line.split("[\\pP\\pZ]"));
            return strings.stream().filter(s->s.trim().length()>0).collect(Collectors.toList());

        });
        documents.take(50).forEach(line -> System.out.println(String.join(" ",line)));

        HashMap<Integer,String> index=new HashMap<>();

        HashingTF hashingTF = new HashingTF();
        JavaRDD<Vector> tf  = hashingTF.transform(documents);
        documents.collect().forEach(strings -> strings.forEach(s -> index.putIfAbsent(hashingTF.indexOf(s),s)));
        // Index documents with unique IDs
        JavaPairRDD<Long, Vector> corpus = JavaPairRDD.fromJavaRDD(tf.zipWithIndex().map(
                new Function<Tuple2<Vector, Long>, Tuple2<Long, Vector>>() {
                    public Tuple2<Long, Vector> call(Tuple2<Vector, Long> doc_id) {
                        return doc_id.swap();
                    }
                }
        ));
        corpus.cache();

        // Cluster the documents into three topics using LDA
        DistributedLDAModel ldaModel = (DistributedLDAModel)new LDA().setK(3).run(corpus);
        LocalLDAModel localLDAModel = ldaModel.toLocal();

        double likelihood = localLDAModel.logLikelihood(corpus);
        double perplexity = localLDAModel.logPerplexity(corpus);
        // Output topics. Each is a distribution over words (matching word count vectors)
        System.out.printf("----------------------Learned topics (as distributions over vocab of " + ldaModel.vocabSize()
                + " words): likelihood  %s perplexity  %s\n",likelihood,perplexity);
        Tuple2<int[], double[]>[] describeTopics = ldaModel.describeTopics(5);
        for (int i = 0; i < describeTopics.length; i++) {

            for (int j = 0; j < describeTopics[i]._1().length; j++) {
                System.out.printf("\"Topic :%s, %s,%s %s\n",i,index.get(describeTopics[i]._1()[j]),describeTopics[i]._1()[j],describeTopics[i]._2()[j]);
            }

        }
//        Matrix topics = ldaModel.topicsMatrix();
//        for (int topic = 0; topic < 5; topic++) {
//            System.out.print("Topic " + topic + ":");
//            for (int word = 0; word < ldaModel.vocabSize(); word++) {
//                System.out.print(" " + topics.apply(word, topic));
//            }
//            System.out.println();
//        }


    }
    private JavaRDD<List<String>> getDocuments() {
        String[] stopWords={"与","及","等","以上范围","行政法规及规章规定须审批项目除外","国家法律","上述范围凡涉及国家法律","国家法律法规规定禁止的项目及应经审批方可经营的项目除外","以上范围凡需审批的","凭有效许可证经营","不得经营","以上凭资质证经营","未获得审批前不得经营","凭有效许可证在核定的范围内经营","凭有效许可证核定范围经营","国家法律法规禁止或者应经审批的项目除外"};
        JavaRDD<List<String>> documents = SparkSupport.getOrCreateSparkContext().textFile("resources/sample_opscope.txt").map(line -> {
            line = line.replaceAll("\\(\\S+\\)","").replaceAll(String .format("(%s)",String.join("|",stopWords))," ");
            List<String> strings = Arrays.asList(line.split("[\\pP\\pZ]"));
            return strings.stream().filter(s->s.trim().length()>0).collect(Collectors.toList());

        });
        documents.take(50).forEach(line -> System.out.println(String.join(" ",line)));
        return documents;
    }
}
