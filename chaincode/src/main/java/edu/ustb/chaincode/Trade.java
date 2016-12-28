package edu.ustb.chaincode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.google.common.collect.Lists;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.shim.ChaincodeBase;
import org.hyperledger.fabric.sdk.shim.ChaincodeStub;
import org.hyperledger.fabric.sdk.transaction.Transaction;
import org.hyperledger.protos.Ca;
import org.hyperledger.protos.TableProto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * trade chaincode
 * Created by zhaoxy on 9/12/16.
 */
public class Trade extends ChaincodeBase {
    private static String tableName = "trade";
    private static Log log = LogFactory.getLog(Trade.class);
    @java.lang.Override
    public String run(ChaincodeStub stub, String function, String[] args) {
        log.info("In run, function:"+function);
        switch (function) {

            case "init":
                init(stub, function, args);
                break;
            case "insert":
                insertRow(stub, args, false);
                break;
            case "update":
                insertRow(stub, args, true);
                break;
            case "delete":
                delete(stub, args);
                break;
            default:
                log.error("No matching case for function:"+function);

        }
        return null;
    }

    private void insertRow(ChaincodeStub stub, String[] args, boolean update) {

        int fieldID = 0;


        try {
            fieldID = Integer.parseInt(args[0]);
        }catch (NumberFormatException e){
            log.error("Illegal field id -" + e.getMessage());
            return;
        }

        TableProto.Column col1 =
                TableProto.Column.newBuilder()
                        .setUint32(fieldID).build();
        TableProto.Column col2 =
                TableProto.Column.newBuilder()
                        .setString(args[1]).build();
        List<TableProto.Column> cols = new ArrayList<TableProto.Column>();
        cols.add(col1);
        cols.add(col2);

        TableProto.Row row = TableProto.Row.newBuilder()
                .addAllColumns(cols)
                .build();
        try {

            boolean success = false;
            if(update){
                success = stub.replaceRow(tableName,row);
            }else
            {
                success = stub.insertRow(tableName, row);
            }
            if (success){
                log.info("Row successfully inserted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String init(ChaincodeStub stub, String function, String[] args) {
        log.info("initing.....");
        List<TableProto.ColumnDefinition> cols = new ArrayList<TableProto.ColumnDefinition>();

        cols.add(TableProto.ColumnDefinition.newBuilder()
                .setName("ID")
                .setKey(true)
                .setType(TableProto.ColumnDefinition.Type.UINT32)
                .build()
        );

        cols.add(TableProto.ColumnDefinition.newBuilder()
                .setName("Name")
                .setKey(false)
                .setType(TableProto.ColumnDefinition.Type.STRING)
                .build()
        );


        try {
            try {
                stub.deleteTable(tableName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stub.createTable(tableName,cols);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean delete(ChaincodeStub stub, String[] args){
        int fieldID = 0;

        try {
            fieldID = Integer.parseInt(args[0]);
        }catch (NumberFormatException e){
            log.error("Illegal field id -" + e.getMessage());
            return false;
        }


        TableProto.Column queryCol =
                TableProto.Column.newBuilder()
                        .setUint32(fieldID).build();
        List<TableProto.Column> key = new ArrayList<>();
        key.add(queryCol);
        return stub.deleteRow(tableName, key);
    }

    @java.lang.Override
    public String query(ChaincodeStub stub, String function, String[] args) {
        log.info("query");
        int fieldID = 0;

        try {
            fieldID = Integer.parseInt(args[0]);
        }catch (NumberFormatException e){
            log.error("Illegal field id -" + e.getMessage());
            return "ERROR querying ";
        }
        TableProto.Column queryCol =
                TableProto.Column.newBuilder()
                        .setUint32(fieldID).build();
        List<TableProto.Column> key = new ArrayList<>();
        key.add(queryCol);
        switch (function){
            case "get": {
                try {
                    TableProto.Row tableRow = stub.getRow(tableName,key);
                    if (tableRow.getSerializedSize() > 0) {
                        return tableRow.getColumns(1).getString();
                    }else
                    {
                        return "No record found !";
                    }
                } catch (Exception invalidProtocolBufferException) {
                    invalidProtocolBufferException.printStackTrace();
                }
            }
            default:
                log.error("No matching case for function:"+function);
                return "";
        }

    }



    public static void chaincode(String[] args) throws Exception {
        log.info("starting");
        new Trade().start(args);
    }

    @Override
    public String getChaincodeID() {
        return "trade";
    }

    public static void main(String[] args) throws Exception{
//        create a chain instance to interact with the network.
        Chain testChain = new Chain("chain1");
//        Add the membership service:
        testChain.setMemberServicesUrl("grpcs://49407ac8f488499d9790e5c4f070c163-ca.us.blockchain.ibm.com:30002", null);

//        Set a keyValueStore:
        testChain.setKeyValStore(new FileKeyValStore("./resources/test.properties"));
//
//        Member admin = new Member("admin", testChain);
//        admin.setEnrollmentSecret("99be6f6137");
//        Enrollment enrollment=new Enrollment();
//        enrollment.setCert("https://blockchain-certs.mybluemix.net/us.blockchain.ibm.com.cert");
//        enrollment.setChainKey("");
//        enrollment.setKey("99be6f6137");
//        admin.setEnrollment(enrollment);
//        testChain.setRegistrar(admin);
//        Add a peer to the chain:
        testChain.addPeer("grpc://49407ac8f488499d9790e5c4f070c163-vp0.us.blockchain.ibm.com:30002", null);
        testChain.addPeer("grpcs://49407ac8f488499d9790e5c4f070c163-vp1.us.blockchain.ibm.com:30002",null);

        testChain.enroll("admin","99be6f6137");



//        Member member = new Member("test_vp0",testChain);
//        RegistrationRequest registrationRequest = new RegistrationRequest();
//        registrationRequest.setEnrollmentID("test_vp0");
//        ArrayList<String> client = new ArrayList(Collections.singletonList("client"));
////        registrationRequest.setRoles(client);
//        registrationRequest.setAffiliation("bank_0");
//        member.register(registrationRequest);
////        Get a member:
//        Member registrar = testChain.getMember("user_type1_0");
//        System.out.println("account:"+registrar.getAccount());
//        System.out.println(registrar.getEnrollmentSecret());
//        Enroll a member:
//        Member member = testChain.enroll("zxy", "password");


    }
}
