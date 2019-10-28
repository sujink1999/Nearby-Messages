package com.sujin.nearbymessages;

import java.util.ArrayList;

public class PopulateCards {

    public void populate(ArrayList<String> cards)
    {
        cards.add("AH");
        for(int i=2; i<11; i++)
        {
            cards.add(i+"H");
        }
        cards.add("KH");
        cards.add("QH");
        cards.add("JH");

        cards.add("AD");
        for(int i=2; i<11; i++)
        {
            cards.add(i+"D");
        }
        cards.add("KD");
        cards.add("QD");
        cards.add("JD");

        cards.add("AC");
        for(int i=2; i<11; i++)
        {
            cards.add(i+"C");
        }
        cards.add("KC");
        cards.add("QC");
        cards.add("JC");

        cards.add("AS");
        for(int i=2; i<11; i++)
        {
            cards.add(i+"S");
        }
        cards.add("KS");
        cards.add("QS");
        cards.add("JS");

    }
}
