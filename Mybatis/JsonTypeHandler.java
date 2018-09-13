package com.laimi.replenishment.handle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * mapper里json型字段到类的映射。
 * 用法一:
 * 入库：#{jsonDataField, typeHandler=com.adu.spring_test.mybatis.typehandler.JsonTypeHandler}
 * 出库：
 * <resultMap>
 * <result property="jsonDataField" column="json_data_field" javaType="com.xxx.MyClass" typeHandler="com.adu.spring_test.mybatis.typehandler.JsonTypeHandler"/>
 * </resultMap>
 *
 * 用法二：
 * 1）在mybatis-config.xml中指定handler:
 *      <typeHandlers>
 *              <typeHandler handler="com.adu.spring_test.mybatis.typehandler.JsonTypeHandler" javaType="com.xxx.MyClass"/>
 *      </typeHandlers>
 * 2)在MyClassMapper.xml里直接select/update/insert。
 *
 *
 * @author yunjie.du
 * @date 2016/5/31 19:33
 */
public class JsonListTypeHandler<T> extends BaseTypeHandler<List<T>> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private Class<T> clazz;

    public JsonListTypeHandler(Class<T> clazz) {
        if (clazz == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, this.toJson(parameter));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName), clazz);
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex), clazz);
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex), clazz);
    }

    private String toJson(List<T> object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<T> toObject(String content, Class<?> clazz) {
        if (content != null && !content.isEmpty()) {
            try {
                List<T>  l = mapper.readValue(content, new TypeReference<List<T>>(){
                    @Override
                    public Type getType() {
                        // return clazz;
                        return ParameterizedTypeImpl.make(List.class, new Type[]{clazz}, null);
                    }
                });
                return l;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    static {
//        mapper.configure(Feature, false);
//        mapper.setSerializationInclusion(Inclusion.NON_NULL);

        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }
}