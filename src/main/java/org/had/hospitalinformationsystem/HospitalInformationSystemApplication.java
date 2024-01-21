package org.had.hospitalinformationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@SpringBootApplication
public class HospitalInformationSystemApplication {

    public static void main(String[] args)  {

        SpringApplication.run(HospitalInformationSystemApplication.class, args);
    }
}
