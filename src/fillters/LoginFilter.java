package fillters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;

@WebFilter("/*")
public class LoginFilter implements Filter {

    public LoginFilter() {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String context_path = ((HttpServletRequest)request).getContextPath();
        String servlet_path = ((HttpServletRequest)request).getServletPath();

        if(!servlet_path.matches("/css.*")) { //cssフォルダないは認証処理から除外
            HttpSession session = ((HttpServletRequest)request).getSession();

            //セッションスコープに保存された従業員（ログインユーザ）情報を取得
            Employee e = (Employee)session.getAttribute("login_employee");

            if(!servlet_path.equals("/login")) {
                //ログアウトしている状態であれば、ログイン画面にリダイレクト
                if(e == null) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/login");
                    return;
                }

                //従業員管理の機能は管理者のみが閲覧できるようにする
                if(servlet_path.matches("/employees.*") && e.getAdmin_flag() == 0) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/");
                    return;
                }
            } else {
                //ログインしているのにログイン画面を表示させようとした場合は、システムのトップページにリダイレクト
                if(e != null) {
                    ((HttpServletResponse)response).sendRedirect(context_path + "/");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }

}
