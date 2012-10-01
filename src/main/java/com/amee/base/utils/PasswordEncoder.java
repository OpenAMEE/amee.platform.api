package com.amee.base.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.amee.base.crypto.InternalCrypto;


public class PasswordEncoder {

    public static void main(String[] args) throws Exception{
        //System.setProperty("amee.saltFile", "D:/amee.realtime.api/server/api/src/main/resources/crypto/amee.salt");
        //System.setProperty("amee.keyFile", "D:/amee.realtime.api/server/api/src/main/resources/crypto/amee.key");

        System.setProperty("amee.saltFile", "/Users/david/code/AMEE/amee.realtime.api/server/api/src/main/resources/crypto/amee.salt");
        System.setProperty("amee.keyFile", "/Users/david/code/AMEE/amee.realtime.api/server/api/src/main/resources/crypto/amee.key");

        System.out.println("Please type a plain-text password:");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        while((input = reader.readLine()) != null && input.length() != 0) {
            System.out.println(InternalCrypto.getAsMD5AndBase64(input));
        }

    }

}
