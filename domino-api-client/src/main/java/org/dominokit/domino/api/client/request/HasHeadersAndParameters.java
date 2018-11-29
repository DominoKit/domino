package org.dominokit.domino.api.client.request;

import org.dominokit.domino.api.shared.request.RequestBean;
import org.dominokit.domino.api.shared.request.ResponseBean;

import java.util.Map;

public interface HasHeadersAndParameters<R extends RequestBean, S extends ResponseBean> {
    HasHeadersAndParameters<R, S> setHeader(String name, String value);

    HasHeadersAndParameters<R, S> setHeaders(Map<String, String> headers);

    HasHeadersAndParameters<R, S> setParameter(String name, String value);

    HasHeadersAndParameters<R, S> setParameters(Map<String, String> parameters);
}
