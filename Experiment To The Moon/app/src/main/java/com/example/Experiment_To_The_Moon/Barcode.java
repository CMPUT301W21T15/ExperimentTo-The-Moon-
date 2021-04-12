package com.example.Experiment_To_The_Moon;

import java.io.Serializable;

/**
 * This class represents a barcode object
 */
public class Barcode implements Serializable {
    public String code;
    public String experiment_name;
    public String result;
    public String type;

    /**
     *
     * @param code
     * the code on the barcode
     * @param experiment_name
     * name of the experiment that the barcode is associated with.
     * @param result
     * result that the barcode will have. e.g. pass/fail, +1 count, 35 non-neg-int.
     * @param type
     * type of the experiment. e.g. Binomial, Count, etc.
     */
    public Barcode(String code, String experiment_name, String result, String type) {
        this.code = code;
        this.experiment_name = experiment_name;
        this.result = result;
        this.type = type;
    }

    /**
     * get the code
     * @return
     * code
     */
    public String getCode() { return this.code; }

    /**
     * get the name
     * @return
     * experiment name
     */
    public String getExperiment_name() { return this.experiment_name; }

    /**
     * get the result
     * @return
     * barcode result
     */
    public String getResult() { return this.result; }

    /**
     * get the type
     * @return
     * experiment type
     */
    public String getType() { return this.type; }
}
