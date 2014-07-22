package com.amee.base.utils;

import com.amee.base.crypto.InternalCrypto;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class PasswordEncoder {

    public static void main(String[] args) throws Exception{

        System.setProperty("amee.saltFile", "/Users/david/code/amee/amee-platform/amee-platform-core/src/main/config/amee.salt");
        System.setProperty("amee.keyFile", "/Users/david/code/amee/amee-platform/amee-platform-core/src/main/config/amee.key");

        System.out.println("Please type a plain-text password:");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        while((input = reader.readLine()) != null && input.length() != 0) {
            System.out.println(InternalCrypto.getAsMD5AndBase64(input));
        }

    }

}
