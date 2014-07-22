package com.amee.base.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Random;
import java.util.Scanner;

/**
 * A utility class for generating and validating AMEE UIDs.
 * <p/>
 * Access is typically via the two singletons, INSTANCE_12 and INSTANCE_16 although custom instances can be created.
 * <p/>
 * Most usages of UidGen are via INSTANCE_12 which provides for simple UIDs in the
 * range  000000000000 to ZZZZZZZZZZZZ. It's possible to create UidGens for other forms of UIDs with varying
 * separators, parts, radix and range.
 *
 * See https://jira.amee.com/browse/PL-3303 for an analysis of collision probability.
 */
public class UidGen implements Serializable {

    /**
     * An instance for creating typical 12 char AMEE UIDs (ranging from 000000000000 to ZZZZZZZZZZZZ).
     */
    public final static UidGen INSTANCE_12 = new UidGen("", 6, 2, 36);

    /**
     * An instance for creating typical 16 char AMEE UIDs (ranging from 0000000000000000 to ZZZZZZZZZZZZZZZZ).
     */
    public final static UidGen INSTANCE_16 = new UidGen("", 8, 2, 36);

    private final static Random RANDOM = new Random();

    private String separator;
    private int parts;
    private int partSize;
    private int radix;
    private int range;

    private UidGen() {
        super();
    }

    /**
     * Create a new instance with custom attributes.
     *
     * @param separator the String to separate parts of the UID
     * @param parts     the number of UID parts
     * @param partSize  the size of each part
     * @param radix     the base or radix of each part
     */
    public UidGen(String separator, int parts, int partSize, int radix) {
        this();
        this.separator = separator;
        this.parts = parts;
        this.partSize = partSize;
        this.radix = radix;
        this.range = (int) Math.pow(radix, partSize);
    }

    /**
     * Generates a 'unique' uid containing values.
     * <p/>
     * Example: 2DF512B4F183
     *
     * @return uid
     */
    public String getUid() {
        StringBuffer uid = new StringBuffer();
        for (int i = 0; i < parts; i++) {
            addUidPart(uid);
            if (i != parts - 1) {
                uid.append(separator);
            }
        }
        return uid.toString().toUpperCase();
    }

    private void addUidPart(StringBuffer uid) {
        StringBuffer part = new StringBuffer();
        part.append(Integer.toString(RANDOM.nextInt(range), this.radix));
        while (part.length() < partSize) {
            part.insert(0, '0'); // left pad with 0's
        }
        if (part.length() > partSize) {
            throw new RuntimeException(
                    "Actual part size (" + part.length() + ") is different to specified part size (" + partSize + ").");
        }
        uid.append(part);
    }

    /**
     * Returns true if the supplied UID matches the rules defined in this UidGen instance.
     *
     * @param uid the UID to validate
     * @return true if the supplied UID matches the rules
     */
    public boolean isValid(String uid) {
        if (uid == null) {
            return false;
        }
        if (uid.length() != (parts * partSize)) {
            return false;
        }
        for (int i = 0; i < uid.length(); i++) {
            if (Character.digit(uid.charAt(i), radix) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Main method to generate a number of UIDs.
     */
    public static void main(String[] args) {
        System.out.print("How many? ");
        Scanner in = new Scanner(System.in);
        int num = in.nextInt();
        for (int i = 0; i < num; i++) {
            System.out.println(INSTANCE_12.getUid());
        }

    }
}