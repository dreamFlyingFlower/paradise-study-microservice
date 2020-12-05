package com.wy.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileinfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public FileinfoExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andFileinfoIdIsNull() {
            addCriterion("fileinfo_id is null");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdIsNotNull() {
            addCriterion("fileinfo_id is not null");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdEqualTo(Integer value) {
            addCriterion("fileinfo_id =", value, "fileinfoId");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdNotEqualTo(Integer value) {
            addCriterion("fileinfo_id <>", value, "fileinfoId");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdGreaterThan(Integer value) {
            addCriterion("fileinfo_id >", value, "fileinfoId");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("fileinfo_id >=", value, "fileinfoId");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdLessThan(Integer value) {
            addCriterion("fileinfo_id <", value, "fileinfoId");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdLessThanOrEqualTo(Integer value) {
            addCriterion("fileinfo_id <=", value, "fileinfoId");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdIn(List<Integer> values) {
            addCriterion("fileinfo_id in", values, "fileinfoId");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdNotIn(List<Integer> values) {
            addCriterion("fileinfo_id not in", values, "fileinfoId");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdBetween(Integer value1, Integer value2) {
            addCriterion("fileinfo_id between", value1, value2, "fileinfoId");
            return (Criteria) this;
        }

        public Criteria andFileinfoIdNotBetween(Integer value1, Integer value2) {
            addCriterion("fileinfo_id not between", value1, value2, "fileinfoId");
            return (Criteria) this;
        }

        public Criteria andLocalNameIsNull() {
            addCriterion("local_name is null");
            return (Criteria) this;
        }

        public Criteria andLocalNameIsNotNull() {
            addCriterion("local_name is not null");
            return (Criteria) this;
        }

        public Criteria andLocalNameEqualTo(String value) {
            addCriterion("local_name =", value, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameNotEqualTo(String value) {
            addCriterion("local_name <>", value, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameGreaterThan(String value) {
            addCriterion("local_name >", value, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameGreaterThanOrEqualTo(String value) {
            addCriterion("local_name >=", value, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameLessThan(String value) {
            addCriterion("local_name <", value, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameLessThanOrEqualTo(String value) {
            addCriterion("local_name <=", value, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameLike(String value) {
            addCriterion("local_name like", value, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameNotLike(String value) {
            addCriterion("local_name not like", value, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameIn(List<String> values) {
            addCriterion("local_name in", values, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameNotIn(List<String> values) {
            addCriterion("local_name not in", values, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameBetween(String value1, String value2) {
            addCriterion("local_name between", value1, value2, "localName");
            return (Criteria) this;
        }

        public Criteria andLocalNameNotBetween(String value1, String value2) {
            addCriterion("local_name not between", value1, value2, "localName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameIsNull() {
            addCriterion("original_name is null");
            return (Criteria) this;
        }

        public Criteria andOriginalNameIsNotNull() {
            addCriterion("original_name is not null");
            return (Criteria) this;
        }

        public Criteria andOriginalNameEqualTo(String value) {
            addCriterion("original_name =", value, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameNotEqualTo(String value) {
            addCriterion("original_name <>", value, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameGreaterThan(String value) {
            addCriterion("original_name >", value, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameGreaterThanOrEqualTo(String value) {
            addCriterion("original_name >=", value, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameLessThan(String value) {
            addCriterion("original_name <", value, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameLessThanOrEqualTo(String value) {
            addCriterion("original_name <=", value, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameLike(String value) {
            addCriterion("original_name like", value, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameNotLike(String value) {
            addCriterion("original_name not like", value, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameIn(List<String> values) {
            addCriterion("original_name in", values, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameNotIn(List<String> values) {
            addCriterion("original_name not in", values, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameBetween(String value1, String value2) {
            addCriterion("original_name between", value1, value2, "originalName");
            return (Criteria) this;
        }

        public Criteria andOriginalNameNotBetween(String value1, String value2) {
            addCriterion("original_name not between", value1, value2, "originalName");
            return (Criteria) this;
        }

        public Criteria andFileTypeIsNull() {
            addCriterion("file_type is null");
            return (Criteria) this;
        }

        public Criteria andFileTypeIsNotNull() {
            addCriterion("file_type is not null");
            return (Criteria) this;
        }

        public Criteria andFileTypeEqualTo(Byte value) {
            addCriterion("file_type =", value, "fileType");
            return (Criteria) this;
        }

        public Criteria andFileTypeNotEqualTo(Byte value) {
            addCriterion("file_type <>", value, "fileType");
            return (Criteria) this;
        }

        public Criteria andFileTypeGreaterThan(Byte value) {
            addCriterion("file_type >", value, "fileType");
            return (Criteria) this;
        }

        public Criteria andFileTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("file_type >=", value, "fileType");
            return (Criteria) this;
        }

        public Criteria andFileTypeLessThan(Byte value) {
            addCriterion("file_type <", value, "fileType");
            return (Criteria) this;
        }

        public Criteria andFileTypeLessThanOrEqualTo(Byte value) {
            addCriterion("file_type <=", value, "fileType");
            return (Criteria) this;
        }

        public Criteria andFileTypeIn(List<Byte> values) {
            addCriterion("file_type in", values, "fileType");
            return (Criteria) this;
        }

        public Criteria andFileTypeNotIn(List<Byte> values) {
            addCriterion("file_type not in", values, "fileType");
            return (Criteria) this;
        }

        public Criteria andFileTypeBetween(Byte value1, Byte value2) {
            addCriterion("file_type between", value1, value2, "fileType");
            return (Criteria) this;
        }

        public Criteria andFileTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("file_type not between", value1, value2, "fileType");
            return (Criteria) this;
        }

        public Criteria andFileSizeIsNull() {
            addCriterion("file_size is null");
            return (Criteria) this;
        }

        public Criteria andFileSizeIsNotNull() {
            addCriterion("file_size is not null");
            return (Criteria) this;
        }

        public Criteria andFileSizeEqualTo(Double value) {
            addCriterion("file_size =", value, "fileSize");
            return (Criteria) this;
        }

        public Criteria andFileSizeNotEqualTo(Double value) {
            addCriterion("file_size <>", value, "fileSize");
            return (Criteria) this;
        }

        public Criteria andFileSizeGreaterThan(Double value) {
            addCriterion("file_size >", value, "fileSize");
            return (Criteria) this;
        }

        public Criteria andFileSizeGreaterThanOrEqualTo(Double value) {
            addCriterion("file_size >=", value, "fileSize");
            return (Criteria) this;
        }

        public Criteria andFileSizeLessThan(Double value) {
            addCriterion("file_size <", value, "fileSize");
            return (Criteria) this;
        }

        public Criteria andFileSizeLessThanOrEqualTo(Double value) {
            addCriterion("file_size <=", value, "fileSize");
            return (Criteria) this;
        }

        public Criteria andFileSizeIn(List<Double> values) {
            addCriterion("file_size in", values, "fileSize");
            return (Criteria) this;
        }

        public Criteria andFileSizeNotIn(List<Double> values) {
            addCriterion("file_size not in", values, "fileSize");
            return (Criteria) this;
        }

        public Criteria andFileSizeBetween(Double value1, Double value2) {
            addCriterion("file_size between", value1, value2, "fileSize");
            return (Criteria) this;
        }

        public Criteria andFileSizeNotBetween(Double value1, Double value2) {
            addCriterion("file_size not between", value1, value2, "fileSize");
            return (Criteria) this;
        }

        public Criteria andFileTimeIsNull() {
            addCriterion("file_time is null");
            return (Criteria) this;
        }

        public Criteria andFileTimeIsNotNull() {
            addCriterion("file_time is not null");
            return (Criteria) this;
        }

        public Criteria andFileTimeEqualTo(String value) {
            addCriterion("file_time =", value, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeNotEqualTo(String value) {
            addCriterion("file_time <>", value, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeGreaterThan(String value) {
            addCriterion("file_time >", value, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeGreaterThanOrEqualTo(String value) {
            addCriterion("file_time >=", value, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeLessThan(String value) {
            addCriterion("file_time <", value, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeLessThanOrEqualTo(String value) {
            addCriterion("file_time <=", value, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeLike(String value) {
            addCriterion("file_time like", value, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeNotLike(String value) {
            addCriterion("file_time not like", value, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeIn(List<String> values) {
            addCriterion("file_time in", values, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeNotIn(List<String> values) {
            addCriterion("file_time not in", values, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeBetween(String value1, String value2) {
            addCriterion("file_time between", value1, value2, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileTimeNotBetween(String value1, String value2) {
            addCriterion("file_time not between", value1, value2, "fileTime");
            return (Criteria) this;
        }

        public Criteria andFileSuffixIsNull() {
            addCriterion("file_suffix is null");
            return (Criteria) this;
        }

        public Criteria andFileSuffixIsNotNull() {
            addCriterion("file_suffix is not null");
            return (Criteria) this;
        }

        public Criteria andFileSuffixEqualTo(String value) {
            addCriterion("file_suffix =", value, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixNotEqualTo(String value) {
            addCriterion("file_suffix <>", value, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixGreaterThan(String value) {
            addCriterion("file_suffix >", value, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixGreaterThanOrEqualTo(String value) {
            addCriterion("file_suffix >=", value, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixLessThan(String value) {
            addCriterion("file_suffix <", value, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixLessThanOrEqualTo(String value) {
            addCriterion("file_suffix <=", value, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixLike(String value) {
            addCriterion("file_suffix like", value, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixNotLike(String value) {
            addCriterion("file_suffix not like", value, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixIn(List<String> values) {
            addCriterion("file_suffix in", values, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixNotIn(List<String> values) {
            addCriterion("file_suffix not in", values, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixBetween(String value1, String value2) {
            addCriterion("file_suffix between", value1, value2, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andFileSuffixNotBetween(String value1, String value2) {
            addCriterion("file_suffix not between", value1, value2, "fileSuffix");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIsNull() {
            addCriterion("createtime is null");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIsNotNull() {
            addCriterion("createtime is not null");
            return (Criteria) this;
        }

        public Criteria andCreatetimeEqualTo(Date value) {
            addCriterion("createtime =", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotEqualTo(Date value) {
            addCriterion("createtime <>", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeGreaterThan(Date value) {
            addCriterion("createtime >", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeGreaterThanOrEqualTo(Date value) {
            addCriterion("createtime >=", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeLessThan(Date value) {
            addCriterion("createtime <", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeLessThanOrEqualTo(Date value) {
            addCriterion("createtime <=", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIn(List<Date> values) {
            addCriterion("createtime in", values, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotIn(List<Date> values) {
            addCriterion("createtime not in", values, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeBetween(Date value1, Date value2) {
            addCriterion("createtime between", value1, value2, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotBetween(Date value1, Date value2) {
            addCriterion("createtime not between", value1, value2, "createtime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}