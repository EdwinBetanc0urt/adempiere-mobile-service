FROM envoyproxy/envoy:v1.28.0

LABEL maintainer="ySenih@erpya.com; EdwinBetanc0urt@outlook.com" \
	description="Proxy Transcoding gRPC to JSON via http"


# Init ENV with default values
ENV \
	SERVER_PORT="5555" \
	BACKEND_HOST="localhost" \
	BACKEND_PORT="50059" \
	SERVICES_ENABLED="template_service.AuthService;"

#Expose Ports
# EXPOSE 9901 # admin port
EXPOSE ${SERVER_PORT}


WORKDIR /etc/envoy/


# Envoy standard configuration
COPY docker/envoy_template.yaml /etc/envoy/envoy_template.yaml

# Proto gRPC descriptor
COPY docker/adempiere-mobile-service-service.pb /data/descriptor.pb
COPY docker/start_grpc_proxy.sh /etc/envoy/start.sh


RUN chmod +x *.sh && \
	chmod +x *.yaml



# Start app
ENTRYPOINT ["sh" , "start.sh"]
