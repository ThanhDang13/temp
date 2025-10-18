//package realm.apps.api.config.vertx;
//
//import org.springframework.stereotype.Component;
//import vn.backend.entity.share.config.vertx.CustomRestConfigurer;
//import vn.backend.spring.vertx.core.config.vertx.security.SecurityResolver;
//
//@Component
//public class RealmCustomRestConfigurer extends CustomRestConfigurer {
//
//    /**
//     * Chia làm 3 vùng:
//     * 1 - vùng truy xuất free (public),
//     * 2 - vùng truy xuất cần authenticate (xác thực là làm được), auth
//     * 3 - vùng truy xuất cần authorize (có quyền cụ thể nào đó mới làm được - cài đặt cụ thể trong từng code)
//     *
//     * @return
//     */
//    @Override
//    public SecurityResolver securityResolver() {
//        return new SecurityResolver()
//                .match("/adminapi/customer/update/partner").free()
//                .match("/adminapi/customer/update/fromPartner").free()
//                ;
//    }
//}
