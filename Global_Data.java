package com.example.chatapp6;

import java.util.ArrayList;

public class GlobalData
{
    private static GlobalData instance;
    private ArrayList<Long> phonelist;
    int i;

    private GlobalData()
    {
        phonelist=new ArrayList<>();
        phonelist.add(9808900808L);
        i=0;
    }
    public static synchronized GlobalData getInstance()
    {
        if(instance==null)
        {
            instance=new GlobalData();
        }
        return instance;
    }
    public ArrayList<Long> getData() {
        return phonelist;
    }
    public int getI()
    {
        return i;
    }
}
