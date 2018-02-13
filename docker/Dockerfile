FROM ibmcom/datapower:latest

MAINTAINER cmardonespino@gmail.com

USER root
ENV DATAPOWER_ACCEPT_LICENSE=true \
    DATAPOWER_INTERACTIVE=true \
    DATAPOWER_WORKER_THREADS=4 \
    DATAPOWER_LOG_STDOUT=true \
    DATAPOWER_LOG_COLOR=true
RUN /bin/sh -c echo "Enabling WebGUI" \
    && sed -i -e '/^web-mgmt/,/^exit/s/admin-state.*/admin-state "enabled"/g' /root/system.cfg \
    && /bin/sh -c echo "Enabling XML-mgmt" \
    && sed -i -e '/^xml-mgmt/,/^exit/s/admin-state.*/admin-state "enabled"/g' /root/system.cfg
CMD ["/bin/drouter"]
