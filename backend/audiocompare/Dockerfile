FROM python:3.10-bookworm

COPY ./main.py /app/main.py
COPY ./start_server.sh /app/start_server.sh

WORKDIR /app

RUN python -m pip install fastapi fastdtw scipy music21 numpy fluidsynth midi2audio uuid
RUN python -m pip install python-multipart uvicorn

RUN apt update
RUN apt install fluidsynth -y


COPY ./fpcalc /app/fpcalc
COPY ./fpcalc-gen /app/fpcalc-gen

RUN chmod 777 /app/fpcalc
RUN chmod 777 /app/fpcalc-gen

EXPOSE 7003

ENTRYPOINT [ "bash", "/app/start_server.sh" ]
