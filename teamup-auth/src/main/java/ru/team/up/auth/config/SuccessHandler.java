//TODO Аргунов М.С. Не забыть удалить, так как в текущей реализации переадресации нет
//package ru.team.up.auth.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import ru.team.up.auth.service.impl.UserDetailsImpl;
//import ru.team.up.core.entity.Account;
//import ru.team.up.core.service.ModeratorsSessionsService;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.Set;
//
//@Slf4j
//@Component
//public class SuccessHandler implements AuthenticationSuccessHandler {
//    private final UserDetailsImpl userService;
//
//    private final ModeratorsSessionsService moderatorsSessionsService;
//    @Autowired
//    public SuccessHandler(UserDetailsImpl userService, ModeratorsSessionsService moderatorsSessionsService) {
//        this.userService = userService;
//        this.moderatorsSessionsService = moderatorsSessionsService;
//    }
//
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
//                                        HttpServletResponse httpServletResponse,
//                                        Authentication authentication) throws IOException, ServletException {
//
//
//        if (authentication.toString().contains ("given_name")) {
//
//            try {
//                Account account = (Account) userService.loadUserByUsername(((DefaultOidcUser)authentication.getPrincipal()).getEmail());
//
//                SecurityContextHolder.getContext().setAuthentication(
//                        new UsernamePasswordAuthenticationToken(
//                                SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
//                                SecurityContextHolder.getContext().getAuthentication().getCredentials(),
//                                Collections.singleton(account.getRole())));
//            } catch (UsernameNotFoundException e) {
//                httpServletResponse.sendRedirect("/oauth2reg");
//                return;
//            }
//        }
//        Account account = (Account) authentication.getPrincipal();
//        log.debug("Успешная авторизация id:{},  email:{}", account.getId(), account.getEmail());
//        Set<String> roles = AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
//        if (roles.contains("ROLE_ADMIN")) {
//            httpServletResponse.sendRedirect("/admin");
//        } else if (roles.contains("ROLE_USER")) {
//            httpServletResponse.sendRedirect("/user");
//        } else if (roles.contains("ROLE_MODERATOR")) {
//            moderatorsSessionsService.createModeratorsSession(account.getId(), LocalDateTime.now());
//            httpServletResponse.sendRedirect("/moderator");
//        } else {
//            httpServletResponse.sendRedirect("/welcome");
//        }
//    }
//}
