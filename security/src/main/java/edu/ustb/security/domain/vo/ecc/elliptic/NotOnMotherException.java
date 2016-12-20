package edu.ustb.security.domain.vo.ecc.elliptic;

import edu.ustb.security.domain.vo.ecc.ECPoint;

public class NotOnMotherException extends Exception{

    private ECPoint sender;

    public NotOnMotherException(ECPoint sender){
	this.sender = sender;
    }

    public String getErrorString(){
	return "NotOnMother";
    }

    public ECPoint getSource(){
	return sender;
    }
}
