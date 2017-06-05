package net.test.predicate;

import com.querydsl.core.BooleanBuilder;
import net.test.entity.QPerson;
import org.springframework.util.StringUtils;

/**
 * @author Zbynek Vavros (zbynek.vavros@i.cz)
 */
public class PersonPredicates extends AbstractPredicates {

    public static final BooleanBuilder nameLike(String value) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (StringUtils.hasText(value)) {
            booleanBuilder.and(QPerson.person.name.like(like(value)));
        }

        return booleanBuilder;
    }

    public static final BooleanBuilder nameEquals(String value) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (StringUtils.hasText(value)) {
            booleanBuilder.and(QPerson.person.name.eq(value));
        }

        return booleanBuilder;
    }
}
