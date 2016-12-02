package controllers.interceptor;

import models.ServicePlan;
import org.jongo.MongoCollection;
import play.mvc.After;
import play.mvc.Controller;

import java.math.BigDecimal;

/**
 *  API Quota limit
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/4/18 9:42
 */
public class BillService extends Controller{

    /**
     * bill
     */
    @After
     static void bill(){
        String clientId = request.params.get("clientId");
        models.Bill bill = new models.Bill();
        bill.setClientId(clientId);
        bill.setOperation(request.action);
        bill.setTarget(request.url);

        ServicePlan servicePlan = getServicePlan(clientId);
        if (servicePlan != null) {
            bill.setFeeRuleId(servicePlan.getIdAsStr());
            bill.setFee( calculateFee(clientId, servicePlan));
            bill.setRawRequest(request.toString());
        }

    }

    private static BigDecimal calculateFee(String clientId, ServicePlan servicePlan) {
        //todo:

        return BigDecimal.ZERO;
    }

    private static ServicePlan getServicePlan(String clientId) {
        //todo:
        MongoCollection servicePlans = ServicePlan.getCollection(ServicePlan.class);
        return servicePlans.findOne().as(ServicePlan.class);
    }

}
