package com.example.Experiment_To_The_Moon;

import java.io.Serializable;

// Barcode class represents a barcode object
public class Barcode implements Serializable {
    public String code;  // the code on the barcode
    public String experiment_name;  // name of the experiment that the barcode is associated with.
    public String result; // result that the barcode will have. e.g. pass/fail, +1 count, 35 non-neg-int.
    public String type;  // type of the experiment. e.g. Binomial, Count, etc.

    public Barcode(String code, String experiment_name, String result, String type) {
        this.code = code;
        this.experiment_name = experiment_name;
        this.result = result;
        this.type = type;
    }

    public String getCode() { return this.code; }
    public String getExperiment_name() { return this.experiment_name; }
    public String getResult() { return this.result; }
    public String getType() { return this.type; }
}
