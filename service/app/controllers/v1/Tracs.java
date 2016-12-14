package controllers.v1;

import models.Trac;
import models.api.Jsonable;
import org.bson.types.ObjectId;
import play.data.validation.Required;
import play.modules.jongo.BaseModel;


/**
 * Created by xudongmei on 2016/12/13.
 */
public class Tracs extends BaseModel implements Jsonable{
    public static void get(@Required Integer type){
        Trac trac =  Trac.getCollection(Trac.class).findOne(new ObjectId()).as(Trac.class);
    }
}
