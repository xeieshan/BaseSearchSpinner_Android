package com.asappstudio.basesearchspinner;

public class GeneralModel {
    public String id;
    public String name;
    public GeneralModel(String id, String name){
        this.id=id;
        this.name=name;
    }


    public String setID(String id){
        return id;
    }
    public String getId(){
        return id;
    }
    public String setName(String name){
        return name;
    }
    public String getName(){
        return name;
    }
}
