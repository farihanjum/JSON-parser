package com.example.jsonparser;

import java.math.BigInteger;

import static java.math.BigInteger.valueOf;

public class secondaryValues {
    long a,b;
    int m;


    public secondaryValues(long a, long b, int m) {
        this.a = a;
        this.b = b;
        this.m = m;
    }

    int countSecondHashValue(long prime,long key){
        BigInteger ak = valueOf(( a*key)%prime);
        BigInteger ak_b =ak.add(valueOf(b));
        BigInteger ak_b_modp =  ak.mod(BigInteger.valueOf(prime));
        int hashValue_of_Key = (ak_b_modp.mod(valueOf(m))).intValue();
        return hashValue_of_Key;
    }
}
