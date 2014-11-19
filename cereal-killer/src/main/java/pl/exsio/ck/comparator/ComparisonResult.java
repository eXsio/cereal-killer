/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.exsio.ck.comparator;

import java.util.Collection;

/**
 *
 * @author exsio
 */
public interface ComparisonResult {

    String[] getFound();

    String[] getNotFound();
}
