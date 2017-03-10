// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.email.checkerImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.email.api.EmailVerifyResult;
import org.talend.dataquality.email.exception.TalendSMTPRuntimeException;

/**
 * created by talend on 2014年12月26日 Detailled comment
 *
 */
public class CallbackMailServerCheckerImpl extends AbstractEmailChecker {

    private static final Logger LOG = Logger.getLogger(CallbackMailServerCheckerImpl.class);

    private static String HEADER = "Email Indicator - "; //$NON-NLS-1$

    private String genericEmailRegex = "^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$"; //$NON-NLS-1$

    private Pattern emailPattern = java.util.regex.Pattern.compile(genericEmailRegex);

    private String dns = null;

    private DirContext ictx = null;

    /**
     * The default port for smtp MX(Mail Exchanger) server
     */
    private int port = 25;

    public CallbackMailServerCheckerImpl() {
        init();
    }

    /**
     * 
     * Write the text ot buffer.
     * 
     * @param wr
     * @param text
     * @throws IOException
     */
    private static void write(BufferedWriter wr, String text) throws IOException {
        wr.write(text + "\r\n"); //$NON-NLS-1$
        wr.flush();
    }

    /**
     * 
     * Get response status's code, 250 means OK, queuing for node node started. Requested mail action okay, completed.
     * See more details at http://email.about.com/cs/standards/a/smtp_error_code_2.htm
     * 
     * @param in
     * @return
     * @throws IOException
     */
    private static int getResponse(BufferedReader in) throws IOException, TalendSMTPRuntimeException {
        String line = null;
        int res = 0;
        do {
            try {
                line = in.readLine();
            } catch (IOException e) {
                line = e.getMessage();
                LOG.warn(line, e);
                continue;
            }
            if (LOG.isInfoEnabled()) {
                LOG.info(line);
            }
            // Make sure the input stream is over and not effect next one read
            if (line == null) {
                break;
            }
            if (res != 0 && line.charAt(3) != '-') {
                continue;
            }
            // if in.ready() is true then line will not be null
            String pfx = line.substring(0, 3);
            try {
                res = Integer.parseInt(pfx);
            } catch (NumberFormatException ex) {
                res = -1;
            }
        } while (in.ready());
        // line.contains("authentication is required") judge whether authentication is required(for example 139.com)
        if (res != 250 && res != 221 && res != 220 || line.contains("authentication is required")) { //$NON-NLS-1$
            throw new TalendSMTPRuntimeException(line);
        }
        return res;
    }

    private List<String> getMX(String hostName) throws NamingException {
        // Perform a DNS lookup for MX records in the domain
        Attributes attrs = ictx.getAttributes(hostName, new String[] { "MX" }); //$NON-NLS-1$
        Attribute attr = attrs.get("MX"); //$NON-NLS-1$
        List<String> res = new ArrayList<String>();

        // if we don't have an MX record, try the machine itself
        if ((attr == null) || (attr.size() == 0)) {
            attrs = ictx.getAttributes(hostName, new String[] { "A" }); //$NON-NLS-1$
            attr = attrs.get("A"); //$NON-NLS-1$
            if (attr == null) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(HEADER + "No match for hostname '" + hostName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                return res;
            }
        }
        // we have machines to try. Return them as an array list
        NamingEnumeration<?> en = attr.getAll();
        Map<Integer, String> map = new TreeMap<Integer, String>();

        while (en.hasMore()) {
            String mailhost;
            String x = (String) en.next();
            String f[] = x.split(" "); //$NON-NLS-1$
            Integer key = 0;
            if (f.length == 1) {
                mailhost = f[0];
            } else if (f[1].endsWith(".")) { //$NON-NLS-1$
                mailhost = f[1].substring(0, f[1].length() - 1);
                key = Integer.valueOf(f[0]);
            } else {
                mailhost = f[1];
                key = Integer.valueOf(f[0]);
            }
            map.put(key, mailhost);
        }
        // NOTE: We SHOULD take the preference into account to be absolutely
        // correct.
        Iterator<Integer> keyInterator = map.keySet().iterator();
        while (keyInterator.hasNext()) {
            res.add(map.get(keyInterator.next()));
        }
        return res;
    }

    public void init() {
        // Prepare naming directory context.
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory"); //$NON-NLS-1$ //$NON-NLS-2$

        // if the user add the paramter for: java.naming.provider.url, if has then add it to env
        // Added TDQ-6918 Allow user add parameter: java.naming.provider.url
        String dnsUrl = dns;
        if (dnsUrl != null) {
            env.put(Context.PROVIDER_URL, dnsUrl);
        } // ~

        try {
            ictx = new InitialDirContext(env);
        } catch (NamingException e) {
            LOG.error("Invalid DNS: " + e); //$NON-NLS-1$
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.IEmailChecker#check(java.lang.String)
     */
    @Override
    public boolean check(String email) throws TalendSMTPRuntimeException {
        if (email == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("mail is empty."); //$NON-NLS-1$
            }
            return false;
        }
        // Find the separator for the domain name
        int pos = email.indexOf('@');
        // If the email does not contain an '@', it's not valid
        if (pos == -1) {
            if (LOG.isInfoEnabled()) {
                LOG.info("no @ charactor in the mail string.");
            }
            return false;
        }

        // check loose email regex
        final Matcher matcher = emailPattern.matcher(email);
        if (!matcher.find()) {
            if (LOG.isInfoEnabled()) {
                LOG.info(HEADER + "Invalid email syntax for " + email); //$NON-NLS-1$
            }
            return false;
        }

        // Isolate the domain/machine name and get a list of mail exchangers
        String domain = email.substring(++pos);
        List<String> mxList = null;
        try {
            mxList = getMX(domain);
        } catch (NamingException ex) {
            if (LOG.isInfoEnabled()) {
                LOG.info(ex.getMessage());
            }
            // talend email on the outside of office room case
            throw new TalendSMTPRuntimeException(ex.getMessage());
        }

        // Just because we can send mail to the domain, doesn't mean that the
        // email is valid, but if we can't, it's a sure sign that it isn't
        if (mxList.isEmpty()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("MX size is 0"); //$NON-NLS-1$
            }
            return false;
        }

        // Now, do the SMTP validation, try each mail exchanger until we get
        // a positive acceptance. It *MAY* be possible for one MX to allow
        // a message [store and forwarder for example] and another [like
        // the actual mail server] to reject it. This is why we REALLY ought
        // to take the preference into account.
        String errorMessage = StringUtils.EMPTY;
        for (int mx = 0; mx < mxList.size(); mx++) {
            try {
                int res;
                Socket skt = new Socket(mxList.get(mx), port);
                BufferedReader rdr = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(skt.getOutputStream()));

                res = getResponse(rdr);
                if (res != 220) { // SMTP Service ready.
                    skt.close();
                    if (LOG.isInfoEnabled()) {
                        LOG.info(HEADER + "Invalid header:" + mxList.get(mx)); //$NON-NLS-1$
                    }
                    return false;
                }
                write(wtr, "EHLO " + domain); //$NON-NLS-1$  

                res = getResponse(rdr);
                if (res != 250) {
                    skt.close();
                    if (LOG.isInfoEnabled()) {
                        LOG.info(HEADER + "Not ESMTP: " + domain); //$NON-NLS-1$
                    }
                    return false;
                }

                // validate the sender email
                write(wtr, "MAIL FROM: <" + email + ">"); //$NON-NLS-1$//$NON-NLS-2$
                res = getResponse(rdr);
                if (res != 250) {
                    skt.close();
                    if (LOG.isInfoEnabled()) {
                        LOG.info(HEADER + "Sender rejected: " + email); //$NON-NLS-1$
                    }
                    return false;
                }

                write(wtr, "RCPT TO: <" + email + ">"); //$NON-NLS-1$//$NON-NLS-2$
                res = getResponse(rdr);

                // be polite
                write(wtr, "RSET"); //$NON-NLS-1$
                getResponse(rdr);
                write(wtr, "QUIT"); //$NON-NLS-1$
                getResponse(rdr);
                if (res != 250) {
                    skt.close();
                    return false;
                }
                rdr.close();
                wtr.close();
                skt.close();
                return true;
            } catch (IOException e) {
                // Do nothing but try next host
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Connection to " + mxList.get(mx) + " failed.", e); //$NON-NLS-1$ //$NON-NLS-2$
                }
                errorMessage = e.getMessage();
                continue;
            }
        }
        throw new TalendSMTPRuntimeException(errorMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.checkerImpl.AbstractEmailChecker#checkEmail(java.lang.String)
     */
    @Override
    public EmailVerifyResult checkEmail(String email) throws TalendSMTPRuntimeException {
        return check(email) ? EmailVerifyResult.VERIFIED : EmailVerifyResult.REJECTED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.checkerImpl.AbstractEmailChecker#check(java.lang.String, java.lang.String[])
     */
    @Override
    public EmailVerifyResult check(String email, String... strings) {
        EmailVerifyResult result = EmailVerifyResult.REJECTED;
        if (check(email)) {
            result = EmailVerifyResult.VERIFIED;
        } else {
            result = EmailVerifyResult.REJECTED;
        }
        return result;
    }

}
