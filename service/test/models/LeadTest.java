package models;

import org.junit.Test;

import java.util.Date;

/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/4/30 17:23
 */
public class LeadTest{

    @Test
    public void toVector() throws Exception {

    }

    @Test
    public void getOpmonths() throws Exception {
            Lead lead = new Lead();
        lead.setOpbegin(new Date(1430386604000l));

        int opmonths = lead.getOpmonths();
        System.err.println(lead.getOpbegin()+":"+opmonths+" months");
    }
}