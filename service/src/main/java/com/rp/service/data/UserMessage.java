package com.rp.service.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author rpradhan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessage {

    private String userName;
    private String message;
    private Date date;
}
