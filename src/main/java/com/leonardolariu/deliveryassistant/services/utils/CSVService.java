package com.leonardolariu.deliveryassistant.services.utils;

import com.leonardolariu.deliveryassistant.payload.errors.ApiException;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CSVService {

    public List<Package> mapToPackageList(MultipartFile file) throws ApiException {
        List<Package> packages = new ArrayList<>();

        if (file.isEmpty()) {
            throw new ApiException(400, "File is empty.");
        } else {
            // parse CSV file to create a list of `Package` objects
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

                // create csv bean reader
                CsvToBean<Package> csvToBean = new CsvToBeanBuilder(reader)
                        .withType(Package.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                // convert `CsvToBean` object to list of packages
                packages = csvToBean.parse();
            } catch (Exception ex) {
                throw new ApiException(400, "File has invalid format.");
            }
        }

        return packages;
    }

    public int getPackagesCount(MultipartFile file) throws ApiException {
        // file is valid! mapToPackageList was called in DeliveryController beforehand

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return (int) (reader.lines().count() - 1);
        } catch (Exception ex) {
            throw new ApiException(400, "File has invalid format.");
        }
    }
}
