import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ru.mail.polis.CheckedOpenHashTableEntity;
import ru.mail.polis.OpenHashTable;
import ru.mail.polis.SimpleStudentGenerator;

/**
 * Created by Nechaev Mikhail
 * Since 12/12/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestHashTable extends AbstractSetTest {

    private Set<CheckedOpenHashTableEntity> validSet;
    private Set<CheckedOpenHashTableEntity> testSet;

    @Before //Запускается перед запуском каждого теста
    public void createSortedSets() {
        validSet = new HashSet<>();
        testSet = new OpenHashTable<>();
    }

    private CheckedOpenHashTableEntity generate() {
        return SimpleStudentGenerator.getInstance().generate();
    }

    @Test
    public void test01() {
        for (int i = 0; i < 5; i++) {
            check(validSet, testSet, generate(), TransformOperation.ADD);
        }
    }

    @Test
    public void test02() {
        List<CheckedOpenHashTableEntity> values = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            CheckedOpenHashTableEntity entity = generate();
            values.add(entity);
            check(validSet, testSet, entity, TransformOperation.ADD);
        }
        for (int i = 19; i >= 0; i--) {
            check(validSet, testSet, values.get(i), TransformOperation.REMOVE);
        }
    }

    @Test
    public void test03() {
        List<CheckedOpenHashTableEntity> values = new ArrayList<>(20);
        for (int i = 0; i < 1000; i++) {
            CheckedOpenHashTableEntity entity = generate();
            values.add(entity);
            check(validSet, testSet, entity, TransformOperation.ADD);
        }
        for (int i = 0; i < 1000; i++) {
            check(validSet, testSet, values.get(i), TransformOperation.REMOVE);
        }
    }

    @Test
    public void test04_01() {
        if (testSet instanceof OpenHashTable) {
            OpenHashTable<CheckedOpenHashTableEntity> openHashTable = (OpenHashTable<CheckedOpenHashTableEntity>) testSet;
            int tableSize = openHashTable.getTableSize();
            CheckedOpenHashTableEntity entity = generate();
            Assert.assertTrue("isHashFunctionValid. tableSize = " + tableSize, entity.isHashFunctionValid(tableSize));
        }
    }

    @Test
    public void test04_02() {
        if (testSet instanceof OpenHashTable) {
            OpenHashTable<CheckedOpenHashTableEntity> openHashTable = (OpenHashTable<CheckedOpenHashTableEntity>) testSet;
            int tableSize = openHashTable.getTableSize();
            for (int i = 0; i < 1000; i++) {
                CheckedOpenHashTableEntity entity = generate();
                openHashTable.add(entity);
                if (tableSize != openHashTable.getTableSize()) {
                    tableSize = openHashTable.getTableSize();
                    Assert.assertTrue("isHashFunctionValid. tableSize = " + tableSize, entity.isHashFunctionValid(tableSize));;
                }
            }
        }
    }

    private void check(Set<CheckedOpenHashTableEntity> validSet, Set<CheckedOpenHashTableEntity> testSet, CheckedOpenHashTableEntity value, TransformOperation transformOperation) {
        checkSizeAndContains(validSet, testSet, value);
        checkTransformOperation(validSet, testSet, value, transformOperation);
        checkSizeAndContains(validSet, testSet, value);
        checkTransformOperation(validSet, testSet, value, transformOperation);
        checkSizeAndContains(validSet, testSet, value);
    }

}
