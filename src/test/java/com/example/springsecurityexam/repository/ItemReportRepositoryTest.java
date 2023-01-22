package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.ItemReport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemReportRepositoryTest {

    @Autowired
    ItemReportRepository repository;

    @AfterEach
    void clear(){
        repository.deleteAllInBatch();
    }

    @Test
    void save() {
//        given
        ItemReport itemReport = new ItemReport(1);

//        when
        ItemReport savedItem = repository.save(itemReport);

//        then
        assertThat(itemReport.getItemId()).usingDefaultComparator().isEqualTo(savedItem.getItemId());

    }

    @Test
    void findNumberOfReportById() {
//        given
        ItemReport itemReport = new ItemReport(1);

//        when
        ItemReport savedItem = repository.save(itemReport);

//        savedItem.setNumberOfReport(savedItem.getNumberOfReport()+2);

        ItemReport resultItem = repository.save(savedItem);

        ItemReport result = repository.findByItemId(resultItem.getItemId());

        List<ItemReport> items = repository.findAll();

//        then
        assertThat(items.size()).isEqualTo(1);
        assertThat(result.getNumberOfReport()).isEqualTo(3);
    }

    @Test
    void findAll() {
//        given
        ItemReport itemReport1 = new ItemReport(1);
        ItemReport itemReport2 = new ItemReport(2);
        ItemReport itemReport3 = new ItemReport(3);

//        when
        repository.save(itemReport1);
        repository.save(itemReport2);
        repository.save(itemReport3);

        List<ItemReport> reports = repository.findAll();

//        then
//        assertThat(reports)
//                .usingRecursiveFieldByFieldElementComparator()
//                .

    }
}