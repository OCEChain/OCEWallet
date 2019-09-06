package com.idea.jgw.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.internal.UnsafeAllocator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyOpenHelper extends SQLiteOpenHelper implements IOpenHelper {
    public MyOpenHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    /**
     * 初始化数据库，一般我们都在这里写语句，现在我们自己封装了方法，就不需要在这里写
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 保存对应的对象到数据库，该类的类名就是插入到数据库的表名
     * @param obj 插入到数据库的对象
     */
    @Override
    public void save(Object obj) {
        //获取类类型
        Class<?> table = obj.getClass();
        //创建对应的表
        createTableIfNotExists(table);
        //具体实现保存数据方法
        save(obj, table, getWritableDatabase());
    }

    /**
     * 保存对应的对象到数据库，该类的类名就是插入到数据库的表名
     * @param obj 插入到数据库的对象
     */
    public void save(String tableName,Object obj) {
        //获取类类型
        Class<?> table = obj.getClass();
        //创建对应的表
        createTableIfNotExists(tableName,table);
        //具体实现保存数据方法
        save(obj, table, getWritableDatabase());
    }

    /**
     *  保存数据的主要操作
     * @param obj 数据库对象
     * @param table 对象类类型
     * @param db 操作数据库
     */
    private void save(Object obj, Class<?> table, SQLiteDatabase db) {
        //将一个对象中的所有字段添加到该数据集中
        ContentValues contentValues = new ContentValues();
        //通过反射获取一个类中的所有属性
        Field[] declaredFields = table.getDeclaredFields();
        //遍历所有的属性
        for (Field field : declaredFields) {
            //获取对应的修饰类型
            int modifiers = field.getModifiers();
            //如果不是静态的就插入到数据库
            if (!Modifier.isStatic(modifiers)) {
                //设置一下数据访问权限为最高级别，也就是public
                field.setAccessible(true);
                try {
                    //将每一个字段的信息保存到数据集中
                    contentValues.put(field.getName(), field.get(obj) + "");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        //对于一般的数据操作，我们采用通常是insert来插入数据，但是为了防止同一个对象的数据进行刷新，所以采用直接替换掉
       try{
           db.replace(table.getName().replaceAll("\\.", "_"), null, contentValues);
       }catch (Exception e){

       }
    }

    /**
     *  保存数据的主要操作
     * @param obj 数据库对象
     * @param table 对象类类型
     * @param db 操作数据库
     */
    private void save(String tableName,Object obj, Class<?> table, SQLiteDatabase db) {
        //将一个对象中的所有字段添加到该数据集中
        ContentValues contentValues = new ContentValues();
        //通过反射获取一个类中的所有属性
        Field[] declaredFields = table.getDeclaredFields();
        //遍历所有的属性
        for (Field field : declaredFields) {
            //获取对应的修饰类型
            int modifiers = field.getModifiers();
            //如果不是静态的就插入到数据库
            if (!Modifier.isStatic(modifiers)) {
                //设置一下数据访问权限为最高级别，也就是public
                field.setAccessible(true);
                try {
                    //将每一个字段的信息保存到数据集中
                    contentValues.put(field.getName(), field.get(obj) + "");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        //对于一般的数据操作，我们采用通常是insert来插入数据，但是为了防止同一个对象的数据进行刷新，所以采用直接替换掉
        db.replace(tableName.replaceAll("\\.", "_"), null, contentValues);
    }


    /**
     * 这里是保存统一对象的多个数据，通过获取集合中的对象，来保存所有的数据
     * @param collection
     */
    @Override
    public void saveAll(Collection collection) {
        //如果集合为空直接不需要操作
        if (collection.isEmpty()) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        //获取该集合的一个对象即可，因为一个集合中保存的都是同一个对象
        Object next = collection.iterator().next();
        //然后创建该对象所对应的表
        createTableIfNotExists(next.getClass());
        //这里为了提高效率采用了事务处理方式，对于事务这里不做过多的讲解
        db.beginTransaction();
        for (Object o : collection) {
            save(o);
        }
        //设置事务为成功状态
        db.setTransactionSuccessful();
        //当事务结束，才会一次性执行上面for中的所有save方法，如果该事务没有结束，则for中的save方法一个都不会执行
        db.endTransaction();
    }

    /**
     * 这里是保存统一对象的多个数据，通过获取集合中的对象，来保存所有的数据
     * @param collection
     */
    @Override
    public void saveAll(String tableName,Collection collection) {
        //如果集合为空直接不需要操作
        if (collection.isEmpty()) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        //获取该集合的一个对象即可，因为一个集合中保存的都是同一个对象
        Object next = collection.iterator().next();
        //然后创建该对象所对应的表
        createTableIfNotExists(tableName,next.getClass());
        //这里为了提高效率采用了事务处理方式，对于事务这里不做过多的讲解
        db.beginTransaction();
        for (Object o : collection) {
            save(tableName,o);
        }
        //设置事务为成功状态
        db.setTransactionSuccessful();
        //当事务结束，才会一次性执行上面for中的所有save方法，如果该事务没有结束，则for中的save方法一个都不会执行
        db.endTransaction();
    }

    /**
     * 通过表名，查询所有的数据，表名对应于类名
     * @param table 类类型
     * @param <T> 泛型参数，任意类型
     * @return
     */
    @Override
    public <T> List<T> queryAll(Class<T> table) {
        //如果该表不存在数据库中，则不需要进行操作
        if (!isTableExists(table)) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();
        //获取表名，因为表名是采用完全包名的形式存储，按照表名规则，不允许有 "." 的存在,所以采用"_"进行替换
        String tableName = table.getName().replaceAll("\\.", "_");
        //通过表名查询所有的数据
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        //通过initList拿到对应的数据
        List<T> result = initList(table, cursor);
        //关闭游标
        cursor.close();
        //返回结果
        return result;
    }


    /**
     * 通过表名，查询所有的数据，表名对应于类名
     * @param table 类类型
     * @param <T> 泛型参数，任意类型
     * @return
     */
    @Override
    public <T> List<T> queryAll(String tableName,Class<T> table) {
        //如果该表不存在数据库中，则不需要进行操作
        if (!isTableExists(tableName)) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();
        //获取表名，因为表名是采用完全包名的形式存储，按照表名规则，不允许有 "." 的存在,所以采用"_"进行替换
         tableName = tableName.replaceAll("\\.", "_");
        //通过表名查询所有的数据
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        //通过initList拿到对应的数据
        List<T> result = initList(table, cursor);
        //关闭游标
        cursor.close();
        //返回结果
        return result;
    }

    /**
     * 通过指定的顺序返回所有查询的结果
     * @param table 类类型
     * @param orderBy 指定顺序
     * @param <T> 泛型参数
     * @return
     */
    @Override
    public <T> List<T> queryAll(Class<T> table, String orderBy) {
        //这里所有的操作和上面类似，就不依依介绍了
        if (!isTableExists(table)) {
            return null;
        }
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table.getName().replaceAll("\\.", "_"), null, null, null, null, null, orderBy);
        List<T> result = initList(table, cursor);
        return result;
    }

    /**
     * 通过指定的顺序返回所有查询的结果
     * @param table 类类型
     * @param orderBy 指定顺序
     * @param <T> 泛型参数
     * @return
     */
    @Override
    public <T> List<T> queryAll(String tableName,Class<T> table, String orderBy) {
        //这里所有的操作和上面类似，就不依依介绍了
        if (!isTableExists(tableName)) {
            return null;
        }
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(tableName.replaceAll("\\.", "_"), null, null, null, null, null, orderBy);
        List<T> result = initList(table, cursor);
        return result;
    }

    /**
     * 通过指定的顺序和查询多少页来查询所有的数据
     * @param table 类类型
     * @param orderBy 指定顺序
     * @param limit 指定的页数
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> queryAll(Class<T> table, String orderBy, int limit) {
        //如上雷同，不做介绍
        if (!isTableExists(table)) {
            return null;
        }
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table.getName().replaceAll("\\.", "_"), null, null, null, null, null, orderBy, String.valueOf(limit));
        List<T> result = initList(table, cursor);
        return result;
    }

    /**
     * 通过id来查询对应的数据
     * @param table
     * @param id
     * @param <T>
     * @return
     */
    @Override
    public <T> T queryById(Class<T> table, Object id) {
        Field idField = null;
        //获取属性id
        idField = getFieldId(table);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table.getName().replaceAll("\\.", "_"),
                null,
                (idField == null ? "_id" : idField.getName()) + " = ?", //判断，如果对应的类中存在id，则通过该类中的id查找数据，如果不存在id就采用使用默认的_id来查询数据
                new String[]{String.valueOf(id)}, null, null, null);
        List<T> list = initList(table, cursor);

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);//这里是通过id查询数据，因为id唯一，所以查到的数据最多也就一个，直接返回list.get(0)
        }
    }

    /**
     * 通过表名清空所有的数据
     * @param table 类类型
     */
    @Override
    public void clear(Class table) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table.getName().replaceAll("\\.","_"), null, null);
    }
    /**
     * 通过表名清空所有的数据
     * @param tableName 类类型
     */
    @Override
    public void clear(String tableName) {

        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(tableName.replaceAll("\\.", "_"), null, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除数据
     * @param obj 指定对象（表）中的数据
     */
    @Override
    public void delete(Object obj) {
        SQLiteDatabase db = getWritableDatabase();
        delete(obj, db);
    }

    /**
     * 主要删除操作，主要是通过id来删除，因为删除一条操作必须有一个唯一列项
     * @param obj 指定对象（表）中的数据
     * @param db
     */
    private void delete(Object obj, SQLiteDatabase db){
        //首先获取该类中的id，如果有就会获取到
        Field idField = getFieldId(obj.getClass());
        //如果不存在属性id，就不需要删除
        if (idField != null) {
            idField.setAccessible(true);
            try {
                db.delete(obj.getClass().getName().replaceAll("\\.", "_"),
                        idField.getName() + " = ?",
                        new String[]{idField.get(obj).toString()});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除集合中所有的对象数据
     * @param collection
     */
    @Override
    public void deleteAll(Collection collection) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        for (Object o : collection) {
            delete(o, db);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 这个方法的主要功能是将数据中查询到的数据放到集合中。
     * 类似于我们查询到对应的数据重新封装到一个对象中，然后把这个对象
     * 放入集合中。这样就能拿到我们的数据集了
     * @param table
     * @param cursor
     * @param <T>
     * @return
     */
    private <T> List<T> initList(Class<T> table, Cursor cursor) {
        List<T> result = new ArrayList<>();
        //这里可能大家不了解，这是Gson为我们提供的一个通过JDK内部API 来创建对象实例，这里不做过多讲解
        UnsafeAllocator allocator = UnsafeAllocator.create();
        while (cursor.moveToNext()) {
            try {
                //创建具体的实例
                T t = allocator.newInstance(table);
                boolean flag = true;
                //遍历所有的游标数据
                for (int i = 0; i < cursor.getColumnCount(); i++) {

                    //每次都去查找该类中有没有自带的id，如果没有，就不应该执行下面的语句
                    //因为下面获取属性名时，有一个异常抛出，要是找不到属性就会结束这个for循环
                    //后面的所有数据就拿不到了,只要检测到没有id，就不需要再检测了。
                    if(flag){
                        Field fieldId = getFieldId(table);
                        if(fieldId == null){
                            flag = !flag;
                            continue;
                        }
                    }
                    //通过列名获取对象中对应的属性名
                    Field field = table.getDeclaredField(cursor.getColumnName(i));
                    //获取属性的类型
                    Class<?> type = field.getType();
                    //设置属性的访问权限为最高权限，因为要设置对应的数据
                    field.setAccessible(true);
                    //获取到数据库中的值，由于sqlite是采用若语法，都可以使用getString来获取
                    String value = cursor.getString(i);
                    //通过判断类型，保存到指定类型的属性中，这里判断了我们常用的数据类型。
                    if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
                        field.set(t, Byte.parseByte(value));
                    } else if (type.equals(Short.class) || type.equals(Short.TYPE)) {
                        field.set(t, Short.parseShort(value));
                    } else if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
                        field.set(t, Integer.parseInt(value));
                    } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
                        field.set(t, Long.parseLong(value));
                    } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
                        field.set(t, Float.parseFloat(value));
                    } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
                        field.set(t, Double.parseDouble(value));
                    } else if (type.equals(Character.class) || type.equals(Character.TYPE)) {
                        field.set(t, value.charAt(0));
                    } else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
                        field.set(t, Boolean.parseBoolean(value));
                    } else if (type.equals(String.class)) {
                        field.set(t, value);
                    }
                }
                result.add(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 判断表格是否存在
     * @param table
     * @return
     */
    private boolean isTableExists(Class table) {
        SQLiteDatabase db = getReadableDatabase();
        //查询表是否存在
        Cursor cursor = db.query("sqlite_master", null, "type = 'table' and name = ?", new String[]{table.getName().replaceAll("\\.", "_")}, null, null, null);
        boolean isExists = cursor.getCount() > 0;
        cursor.close();
        return isExists;
    }

    /**
     * 判断表格是否存在
     * @param table
     * @return
     */
    private boolean isTableExists(String table) {
        SQLiteDatabase db = getReadableDatabase();
        //查询表是否存在
        Cursor cursor = db.query("sqlite_master", null, "type = 'table' and name = ?", new String[]{table.replaceAll("\\.", "_")}, null, null, null);
        boolean isExists = cursor.getCount() > 0;
        cursor.close();
        return isExists;
    }

    /**
     * 如果表格不存在就创建该表。如果存在就不创建
     * @param table
     */
    private void createTableIfNotExists(Class table) {
        createTableIfNotExists(table.getName().replaceAll("\\.", "_"), table);
//        if (!isTableExists(table)) {
//            SQLiteDatabase db = getWritableDatabase();
//            StringBuilder builder = new StringBuilder();
//            builder.append("CREATE TABLE IF NOT EXISTS ");
//            builder.append(table.getName().replaceAll("\\.", "_"));
//            builder.append(" (");
//            Field id = getFieldId(table);
//            if (id == null) {
//                builder.append("_id Integer PRIMARY KEY AUTOINCREMENT,");
//            } else {
//                builder.append(id.getName()).append("  PRIMARY KEY, ");
//            }
//            for (Field field : table.getDeclaredFields()) {
//                int modifiers = field.getModifiers();
//                if (!field.equals(id) && !Modifier.isStatic(modifiers)) {
//                    builder.append(field.getName()).append(" TEXT").append(",");
//                }
//            }
//            builder.deleteCharAt(builder.length() - 1);
//            builder.append(")");
//            db.execSQL(builder.toString());
//        }
    }

    /**
     * 如果表格不存在就创建该表。如果存在就不创建
     * @param table
     */
    private void createTableIfNotExists(String tableName,Class table) {
        if (!isTableExists(table)) {
            SQLiteDatabase db = getWritableDatabase();
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS ");
            builder.append(tableName.replaceAll("\\.", "_"));
            builder.append(" (");
            Field id = getFieldId(table);
            if (id == null) {
                builder.append("_id Integer PRIMARY KEY AUTOINCREMENT,");
            } else {
                builder.append(id.getName()).append("  PRIMARY KEY, ");
            }
            for (Field field : table.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (!field.equals(id) && !Modifier.isStatic(modifiers)) {
                    builder.append(field.getName()).append(" TEXT").append(",");
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(")");
            db.execSQL(builder.toString());
        }
    }

    /**
     * 获取对象属性中的id字段，如果有就获取，没有就不获取
     * @param table
     * @return
     */
    private Field getFieldId(Class table) {
        Field fieldId = null;
        try {
            fieldId = table.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (fieldId == null) {
            try {
                fieldId = table.getDeclaredField("_id");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return fieldId;
    }
}