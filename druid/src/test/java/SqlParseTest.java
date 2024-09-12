import org.junit.Test;

public class SqlParseTest {

    @Test
    public void test0() throws Exception {
        String sql = "select * from user_items t where user_id=1 order by created_at limit 3 offset 10";
        String a = ParseUtil.parseAndReplaceTableNames(sql, "mysql");
        System.out.println(a);
    }

    @Test
    public void test1() throws Exception {
        String sql = "select * from user_items, (select 1 from config where 1=1) t where user_id=1 order by created_at limit 3 offset 10";
        String a = ParseUtil.parseAndReplaceTableNames(sql, "mysql");
        System.out.println(a);
    }

    @Test
    public void test2() throws Exception {
        String sql = "select *, (select 1 from config where 1=1) a from user_items, (select 1 from config where 1=1) t where user_id=1 order by created_at limit 3 offset 10";
        String a = ParseUtil.parseAndReplaceTableNames(sql, "mysql");
        System.out.println(a);
    }

    @Test
    public void test4() throws Exception {
        String sql = "select *, (select 1 from config where 1=1) a from user_items a inner join config b on a.id = b.id where user_id=1 order by created_at limit 3 offset 10";
        String a = ParseUtil.parseAndReplaceTableNames(sql, "mysql");
        System.out.println(a);
    }

    @Test
    public void test5() throws Exception {
        String sql = "select *, (select 1 from config where 1=1) a from user_items a inner join config b on a.id = b.id where user_id in (select 1 from config where 1=1) order by created_at limit 3 offset 10";
        String a = ParseUtil.parseAndReplaceTableNames(sql, "mysql");
        System.out.println(a);
    }

    @Test
    public void test6() throws Exception {
        String sql = "select *, (select 1 from config where 1 in (select 1 from config where 1 in (select 1 from config where 1=1))) a from user_items a inner join config on a.id = config.id where user_id=1 order by created_at limit 3 offset 10";
        String a = ParseUtil.parseAndReplaceTableNames(sql, "mysql");
        System.out.println(a);
    }

    @Test
    public void test67() throws Exception {
        String sql = "select * from xsky_instance_detail a where exists(select * from xsky_instance b where xsky_instance.id like '10.244.2.106%')";
        String a = ParseUtil.parseAndReplaceTableNames(sql, "mysql");
        System.out.println(a);
    }
}



