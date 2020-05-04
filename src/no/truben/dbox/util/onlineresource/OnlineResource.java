/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.truben.dbox.util.onlineresource;

import no.truben.dbox.model.ApplicationBean;

/**
 *
 * @author truben
 */
public interface OnlineResource {

    ApplicationBean fillInInformation(ApplicationBean di);

    String getDeveloper();

    String getGenre();

    String getName();

    String getPublisher();

    String getYear();

    boolean isValid();
    
}
