package org.ei.drishti.contract;

public class ANCRequest {

        private String caseid;
        private String tetanus1;
        private String tetanus2;
        private String iron1;
        private String iron2;
        private String iron3;

        public ANCRequest() {
         }


        public String caseid() {
            return caseid;
        }

    @Override
    public String toString() {
        return "ANCRequest{" +
                "caseid='" + caseid + '\'' +
                ", tetanus1='" + tetanus1 + '\'' +
                ", tetanus2='" + tetanus2 + '\'' +
                ", iron1='" + iron1 + '\'' +
                ", iron2='" + iron2 + '\'' +
                ", iron3='" + iron3 + '\'' +
                '}';
    }
}

