/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.exsio.ck.comparator;

import java.util.Collection;

public class ComparisonResultImpl implements ComparisonResult {

    private final String[] found;

    private final String[] notFound;

    public ComparisonResultImpl(String[] found, String[] notFound) {
        this.found = found;
        this.notFound = notFound;
    }

    @Override
    public  String[] getFound() {
        return this.found;
    }

    @Override
    public  String[] getNotFound() {
        return this.notFound;
    }

}
