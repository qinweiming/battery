package controllers.v1;

import models.Track;
import models.api.Jsonable;
import org.bson.types.ObjectId;
import play.data.validation.Required;
import play.modules.jongo.BaseModel;


/**
 * Created by xudongmei on 2016/12/13.
 */
public class Tracks extends BaseModel implements Jsonable{
    public static void get(@Required Integer type){
        Track track =  Track.getCollection(Track.class).findOne(new ObjectId()).as(Track.class);
    }

}
