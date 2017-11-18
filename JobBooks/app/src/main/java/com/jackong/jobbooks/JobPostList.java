package com.jackong.jobbooks;

public class JobPostList {
    String job_spec;
    String job_title;
    String job_desc;
    String job_id;
    String job_time;

    public JobPostList (String spec, String title, String desc, String id, String time){
        this.job_spec = spec;
        this.job_title = title;
        this.job_desc = desc;
        this.job_id = id;
        this.job_time = time;
    }

    public String getJob_spec(){
        return job_spec;
    }

    public String getJob_title(){
        return job_title;
    }

    public String getJob_desc(){
        return job_desc;
    }

    public String getJob_id() {
        return job_id;
    }

    public String getJob_time() {
        return job_time;
    }
}
