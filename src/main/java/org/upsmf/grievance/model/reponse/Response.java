package org.upsmf.grievance.model.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {

    private int status;

    private Object body;

}
