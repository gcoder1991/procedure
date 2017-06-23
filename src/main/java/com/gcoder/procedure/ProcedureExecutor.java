package com.gcoder.procedure;

/**
 * Created by gcoder on 2017/6/23.
 */
public class ProcedureExecutor {

    public static int execute(Procedure procedure) {
        procedure.execute();
        return procedure.getRetCode();
    }

}
