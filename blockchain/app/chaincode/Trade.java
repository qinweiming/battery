package chaincode;

import org.hyperledger.fabric.sdk.Chain;
import org.hyperledger.fabric.sdk.FileKeyValStore;
import org.hyperledger.fabric.sdk.Member;
import org.hyperledger.fabric.sdk.shim.ChaincodeBase;
import org.hyperledger.fabric.sdk.shim.ChaincodeStub;

/**
 * trade chaincode
 * Created by zhaoxy on 9/12/16.
 */
public class Trade extends ChaincodeBase {
    @Override
    public String run(ChaincodeStub chaincodeStub, String s, String[] strings) {

        return null;
    }

    @Override
    public String query(ChaincodeStub chaincodeStub, String s, String[] strings) {

        return null;
    }

    @Override
    public String getChaincodeID() {
        return "trade";
    }

    public static void main(String[] args) throws Exception{
//        create a chain instance to interact with the network.
        Chain testChain = new Chain("chain1");
//        Add the membership service:
        testChain.setMemberServicesUrl("grpc://localhost:7054", null);

//        Set a keyValueStore:
        testChain.setKeyValStore(new FileKeyValStore(System.getProperty("user.home")+"/test.properties"));

//        Add a peer to the chain:
        testChain.addPeer("grpc://localhost:7051", null);

//        Get a member:
        Member registrar = testChain.getMember("admin");
//        System.out.println("account:"+testChain.getRegistrar().getAccount());

//        Enroll a member:
        Member member = testChain.enroll("user", "secret");


    }
}
