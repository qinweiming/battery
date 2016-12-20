package edu.ustb.security.domain.vo.ecc.elliptic;

public class NoCommonMotherException extends Exception{

    public String getErrorString(){
	return "NoCommonMother";
    }

}
