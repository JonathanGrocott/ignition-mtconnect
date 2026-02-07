System.register("com.inductiveautomation.mtconnect.gateway", ["react"], function (_export) {
  "use strict";

  var React;

  return {
    setters: [
      function (_react) {
        React = _react && _react.default ? _react.default : _react;
      }
    ],
    execute: function () {
      const e = React.createElement;
      const { useEffect, useMemo, useState } = React;

            const styleText = `
:root {
  --mtc-ink: #121314;
  --mtc-ink-soft: #2a2d33;
  --mtc-cream: #f5f1ea;
  --mtc-sand: #efe7db;
  --mtc-amber: #f1b44b;
  --mtc-teal: #3aa6a0;
  --mtc-rose: #d05b5b;
  --mtc-shadow: rgba(17, 20, 30, 0.15);
  --mtc-card: rgba(255, 255, 255, 0.82);
}

.mtc-root {
  font-family: "Space Grotesk", "IBM Plex Sans", "Segoe UI", sans-serif;
  color: var(--mtc-ink);
  min-height: 100vh;
  padding: 32px 36px 60px;
  background:
    radial-gradient(1000px 600px at 10% 10%, rgba(241, 180, 75, 0.25), transparent 60%),
    radial-gradient(900px 600px at 85% 15%, rgba(58, 166, 160, 0.2), transparent 60%),
    linear-gradient(135deg, #f8f1e7 0%, #f1ebe1 45%, #f7efe3 100%);
  position: relative;
  overflow: hidden;
}

.mtc-root::before {
  content: "";
  position: absolute;
  inset: -120px;
  background: conic-gradient(from 180deg, rgba(58, 166, 160, 0.07), rgba(241, 180, 75, 0.08), rgba(208, 91, 91, 0.05));
  filter: blur(40px);
  opacity: 0.65;
  pointer-events: none;
}

.mtc-hero {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 28px;
}

.mtc-title {
  font-size: clamp(28px, 4vw, 44px);
  font-weight: 700;
  letter-spacing: -0.02em;
}

.mtc-subtitle {
  font-size: 14px;
  color: var(--mtc-ink-soft);
  max-width: 420px;
}

.mtc-summary {
  background: var(--mtc-card);
  border-radius: 14px;
  padding: 14px 18px;
  box-shadow: 0 12px 30px var(--mtc-shadow);
  font-size: 13px;
}

.mtc-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 18px;
}

.mtc-card {
  background: var(--mtc-card);
  border-radius: 18px;
  padding: 16px 18px 18px;
  box-shadow: 0 18px 30px var(--mtc-shadow);
  border: 1px solid rgba(18, 19, 20, 0.05);
  animation: mtc-rise 0.6s ease both;
}

.mtc-card h3 {
  margin: 4px 0 10px;
  font-size: 18px;
  font-weight: 600;
}

.mtc-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 12px;
  color: var(--mtc-ink-soft);
}

.mtc-actions {
  margin-top: 12px;
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.mtc-meta .mtc-button {
  margin-top: 6px;
  margin-right: 6px;
}

.mtc-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.mtc-form label {
  font-size: 12px;
  font-weight: 600;
  color: var(--mtc-ink-soft);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.mtc-input {
  margin-top: 6px;
  padding: 8px 10px;
  border-radius: 10px;
  border: 1px solid rgba(18, 19, 20, 0.15);
  background: white;
  font-size: 13px;
}

.mtc-form-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.mtc-button {
  border: none;
  padding: 8px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
  background: var(--mtc-ink);
  color: white;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0 8px 18px rgba(18, 19, 20, 0.18);
}

.mtc-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

.mtc-button:hover:not(:disabled) {
  transform: translateY(-1px);
}

.mtc-test-result {
  font-size: 12px;
  color: var(--mtc-ink-soft);
}

.mtc-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.mtc-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(18, 19, 20, 0.08);
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.mtc-chip.alert {
  background: rgba(208, 91, 91, 0.15);
  color: #8f2f2f;
}

.mtc-chip.warn {
  background: rgba(241, 180, 75, 0.18);
  color: #7a4a00;
}

.mtc-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--mtc-teal);
  box-shadow: 0 0 0 4px rgba(58, 166, 160, 0.2);
}

.mtc-dot.error {
  background: var(--mtc-rose);
  box-shadow: 0 0 0 4px rgba(208, 91, 91, 0.2);
}

.mtc-dot.stopped {
  background: #7b7e86;
  box-shadow: 0 0 0 4px rgba(123, 126, 134, 0.2);
}

.mtc-empty {
  font-size: 14px;
  color: var(--mtc-ink-soft);
  padding: 24px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 16px;
}

@keyframes mtc-rise {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 720px) {
  .mtc-root {
    padding: 24px 18px 40px;
  }

  .mtc-hero {
    flex-direction: column;
    align-items: flex-start;
  }
}
`;

            const styleTag = document.createElement("style");
            styleTag.textContent = `@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;600;700&display=swap');\n${styleText}`;
            document.head.appendChild(styleTag);

            function useStatusPoll(url, interval) {
              const [state, setState] = useState({ connections: [], updatedAt: null, error: null, csrfToken: null });

                useEffect(() => {
                    let alive = true;
                    let timer = null;

                    const fetchStatus = () => {
                        fetch(url, { credentials: "same-origin" })
                            .then(response => response.ok ? response.json() : Promise.reject(response.statusText))
                            .then(payload => {
                                if (!alive) {
                                    return;
                                }
                                setState({
                                  connections: payload.connections || [],
                                  updatedAt: new Date().toISOString(),
                                  error: null,
                                  csrfToken: payload.csrfToken || null
                                });
                            })
                            .catch(err => {
                                if (!alive) {
                                    return;
                                }
                                setState(prev => ({ ...prev, error: String(err || "Request failed") }));
                            });
                    };

                    fetchStatus();
                    timer = setInterval(fetchStatus, interval);

                    return () => {
                        alive = false;
                        if (timer) {
                            clearInterval(timer);
                        }
                    };
                }, [url, interval]);

                return state;
            }

            function MtconnectStatus() {
              const status = useStatusPoll("/data/mtconnect/status", 5000);
              const connections = status.connections || [];
              const csrfToken = status.csrfToken || null;
              const [tests, setTests] = useState({});
              const [devicesByName, setDevicesByName] = useState({});
              const [deviceErrors, setDeviceErrors] = useState({});
              const [deviceActions, setDeviceActions] = useState({});

                const summary = useMemo(() => {
                    const running = connections.filter(item => item.running).length;
                    const withErrors = connections.filter(item => item.lastError).length;
                    return { running, withErrors, total: connections.length };
                }, [connections]);

                const runTest = (name) => {
                  if (!name) {
                    return;
                  }
                  setTests(prev => ({ ...prev, [name]: { state: "pending" } }));
                  fetch(`/data/mtconnect/test?name=${encodeURIComponent(name)}`, { credentials: "same-origin" })
                    .then(response => response.ok ? response.json() : Promise.reject(response.statusText))
                    .then(payload => {
                      setTests(prev => ({
                        ...prev,
                        [name]: {
                          state: payload.success ? "success" : "error",
                          message: payload.message || "Unknown",
                          timestamp: payload.timestamp || null
                        }
                      }));
                    })
                    .catch(err => {
                      setTests(prev => ({
                        ...prev,
                        [name]: { state: "error", message: String(err || "Request failed") }
                      }));
                    });
                };

                const loadDevices = (name) => {
                  if (!name) {
                    return;
                  }
                  setDeviceErrors(prev => ({ ...prev, [name]: null }));
                  fetch(`/data/mtconnect/devices?name=${encodeURIComponent(name)}`, { credentials: "same-origin" })
                    .then(response => response.ok ? response.json() : Promise.reject(response.statusText))
                    .then(payload => {
                      if (payload && payload.success) {
                        setDevicesByName(prev => ({ ...prev, [name]: payload.devices || [] }));
                      } else {
                        setDeviceErrors(prev => ({ ...prev, [name]: payload.message || "Probe failed" }));
                      }
                    })
                    .catch(err => {
                      setDeviceErrors(prev => ({ ...prev, [name]: String(err || "Request failed") }));
                    });
                };

                const useDevice = (connectionName, deviceName) => {
                  if (!connectionName || !deviceName) {
                    return;
                  }
                  if (!csrfToken) {
                    setDeviceActions(prev => ({ ...prev, [connectionName]: "Missing CSRF token" }));
                    return;
                  }
                  setDeviceActions(prev => ({ ...prev, [connectionName]: "Updating..." }));
                  const body = new URLSearchParams({ name: connectionName, device: deviceName });
                  fetch("/data/mtconnect/set-device", {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                      "Content-Type": "application/x-www-form-urlencoded",
                      "X-CSRF-Token": csrfToken
                    },
                    body: body.toString()
                  })
                    .then(response => response.ok ? response.json() : Promise.reject(response.statusText))
                    .then(payload => {
                      const message = payload && payload.message ? payload.message : "Updated";
                      setDeviceActions(prev => ({ ...prev, [connectionName]: message }));
                    })
                    .catch(err => {
                      setDeviceActions(prev => ({ ...prev, [connectionName]: String(err || "Request failed") }));
                    });
                };

                return e("div", { className: "mtc-root" },
                    e("div", { className: "mtc-hero" },
                        e("div", null,
                            e("div", { className: "mtc-title" }, "MTConnect Status"),
                            e("div", { className: "mtc-subtitle" },
                                "Live status for MTConnect connections. Data refreshes every 5 seconds."
                            )
                        ),
                        e("div", { className: "mtc-summary" },
                            e("div", null, `Connections: ${summary.total}`),
                            e("div", null, `Running: ${summary.running}`),
                            e("div", null, `Errors: ${summary.withErrors}`)
                        )
                    ),
                    status.error && e("div", { className: "mtc-empty" },
                        `Status fetch error: ${status.error}`
                    ),
                    connections.length === 0 && !status.error
                      ? e("div", { className: "mtc-empty" }, "No MTConnect connections configured yet. Use the Connections page to add one.")
                        : e("div", { className: "mtc-grid" },
                            connections.map((item, index) => {
                                const hasError = Boolean(item.lastError);
                                const running = Boolean(item.running);
                                const connected = Boolean(item.connected);
                                const inBackoff = (item.backoffRemainingSeconds || 0) > 0;
                                const badgeLabel = !running ? "Stopped" : (hasError ? "Error" : (connected ? "Connected" : "Connecting"));
                                const dotClass = !running ? "mtc-dot stopped" : (hasError ? "mtc-dot error" : "mtc-dot");
                                const testState = tests[item.name] || {};
                                const deviceList = devicesByName[item.name] || [];
                                const deviceError = deviceErrors[item.name];
                                const deviceAction = deviceActions[item.name];
                                const delay = `${index * 40}ms`;

                                return e("div", { className: "mtc-card", key: item.name || index, style: { animationDelay: delay } },
                                    e("div", { className: "mtc-badge" },
                                        e("span", { className: dotClass }),
                                      badgeLabel,
                                      inBackoff && e("span", { className: "mtc-chip warn" }, `Backoff ${item.backoffRemainingSeconds}s`)
                                    ),
                                    e("h3", null, item.name || "Unnamed Connection"),
                                    e("div", { className: "mtc-meta" },
                                        e("div", null, `Agent: ${item.agentUrl || "n/a"}`),
                                        e("div", null, `Device: ${item.deviceName || "(all)"}`),
                                        e("div", null, `Interval: ${item.pollIntervalMs || "?"} ms`),
                                      e("div", null, `Connected: ${connected ? "yes" : "no"}`),
                                      e("div", null, `Uptime: ${item.uptimeSeconds || 0} s`),
                                      e("div", null, `Failures: ${item.failureCount || 0}`),
                                      e("div", null, `Last success: ${item.lastSuccessTime || "never"}`),
                                        e("div", null, `Last poll: ${item.lastPollTime || "never"}`),
                                      e("div", null, `Last error: ${item.lastError || "none"}`),
                                      e("div", null, `Observed tags: ${item.lastObservedTagCount || 0} / ${item.totalTagCount || 0}`),
                                      deviceList.length > 0 && e("div", null,
                                        "Devices:",
                                        e("div", null,
                                          deviceList.map(device => {
                                            const label = device.name || device.uuid || device.id || "Unknown";
                                            return e("button", {
                                              key: device.id || label,
                                              className: "mtc-button",
                                              onClick: () => useDevice(item.name, device.name || device.uuid || device.id)
                                            }, `Use ${label}`);
                                          })
                                        )
                                      ),
                                      deviceError && e("div", null, `Devices error: ${deviceError}`)
                                    ),
                                    e("div", { className: "mtc-actions" },
                                      e("button", {
                                        className: "mtc-button",
                                        disabled: !item.name || testState.state === "pending",
                                        onClick: () => runTest(item.name)
                                      }, testState.state === "pending" ? "Testing..." : "Test Connection"),
                                      e("button", {
                                        className: "mtc-button",
                                        disabled: !item.name,
                                        onClick: () => loadDevices(item.name)
                                      }, deviceList.length > 0 ? "Reload Devices" : "Load Devices"),
                                      deviceAction && e("div", { className: "mtc-test-result" }, deviceAction),
                                      testState.message && e("div", { className: "mtc-test-result" },
                                        `${testState.state === "success" ? "OK" : "Fail"}: ${testState.message}`
                                      )
                                    )
                                );
                            })
                        )
                );
            }

            function MtconnectConnections() {
              const connectionState = useStatusPoll("/data/mtconnect/connections", 5000);
              const connections = connectionState.connections || [];
              const csrfToken = connectionState.csrfToken || null;
              const [form, setForm] = useState({
                name: "",
                agentUrl: "http://localhost:5000",
                deviceName: "",
                pollIntervalMs: "1000",
                tagProviderName: "MTConnect"
              });
              const [actionMessage, setActionMessage] = useState(null);

              const updateField = (key, value) => {
                setForm(prev => ({ ...prev, [key]: value }));
              };

              const submit = (path) => {
                if (!csrfToken) {
                  setActionMessage("Missing CSRF token");
                  return;
                }
                const body = new URLSearchParams({
                  name: form.name || "",
                  agentUrl: form.agentUrl || "",
                  deviceName: form.deviceName || "",
                  pollIntervalMs: form.pollIntervalMs || "",
                  tagProviderName: form.tagProviderName || ""
                });
                fetch(path, {
                  method: "POST",
                  credentials: "same-origin",
                  headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "X-CSRF-Token": csrfToken
                  },
                  body: body.toString()
                })
                  .then(response => response.ok ? response.json() : Promise.reject(response.statusText))
                  .then(payload => {
                    setActionMessage(payload && payload.message ? payload.message : "Done");
                  })
                  .catch(err => {
                    setActionMessage(String(err || "Request failed"));
                  });
              };

              const removeConnection = (name) => {
                if (!csrfToken) {
                  setActionMessage("Missing CSRF token");
                  return;
                }
                const body = new URLSearchParams({ name: name || "" });
                fetch("/data/mtconnect/connections/delete", {
                  method: "POST",
                  credentials: "same-origin",
                  headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "X-CSRF-Token": csrfToken
                  },
                  body: body.toString()
                })
                  .then(response => response.ok ? response.json() : Promise.reject(response.statusText))
                  .then(payload => {
                    setActionMessage(payload && payload.message ? payload.message : "Deleted");
                  })
                  .catch(err => {
                    setActionMessage(String(err || "Request failed"));
                  });
              };

              const loadIntoForm = (item) => {
                setForm({
                  name: item.name || "",
                  agentUrl: item.agentUrl || "",
                  deviceName: item.deviceName || "",
                  pollIntervalMs: String(item.pollIntervalMs || ""),
                  tagProviderName: item.tagProviderName || ""
                });
              };

              return e("div", { className: "mtc-root" },
                e("div", { className: "mtc-hero" },
                  e("div", null,
                    e("div", { className: "mtc-title" }, "MTConnect Connections"),
                    e("div", { className: "mtc-subtitle" }, "Create and manage MTConnect agent connections.")
                  ),
                  e("div", { className: "mtc-summary" },
                    e("div", null, `Connections: ${connections.length}`)
                  )
                ),
                e("div", { className: "mtc-card" },
                  e("h3", null, "Connection Settings"),
                  e("div", { className: "mtc-form" },
                    e("div", null,
                      e("label", null, "Name"),
                      e("input", {
                        className: "mtc-input",
                        value: form.name,
                        onChange: event => updateField("name", event.target.value)
                      })
                    ),
                    e("div", null,
                      e("label", null, "Agent URL"),
                      e("input", {
                        className: "mtc-input",
                        value: form.agentUrl,
                        onChange: event => updateField("agentUrl", event.target.value)
                      })
                    ),
                    e("div", null,
                      e("label", null, "Device Name"),
                      e("input", {
                        className: "mtc-input",
                        value: form.deviceName,
                        placeholder: "(all)",
                        onChange: event => updateField("deviceName", event.target.value)
                      })
                    ),
                    e("div", null,
                      e("label", null, "Poll Interval (ms)"),
                      e("input", {
                        className: "mtc-input",
                        value: form.pollIntervalMs,
                        onChange: event => updateField("pollIntervalMs", event.target.value)
                      })
                    ),
                    e("div", null,
                      e("label", null, "Tag Provider"),
                      e("input", {
                        className: "mtc-input",
                        value: form.tagProviderName,
                        onChange: event => updateField("tagProviderName", event.target.value)
                      })
                    )
                  ),
                  e("div", { className: "mtc-actions" },
                    e("button", {
                      className: "mtc-button",
                      onClick: () => submit("/data/mtconnect/connections/create")
                    }, "Create"),
                    e("button", {
                      className: "mtc-button",
                      onClick: () => submit("/data/mtconnect/connections/update")
                    }, "Update"),
                    actionMessage && e("div", { className: "mtc-test-result" }, actionMessage)
                  )
                ),
                connections.length === 0
                  ? e("div", { className: "mtc-empty" }, "No connections yet. Create one above.")
                  : e("div", { className: "mtc-grid" },
                      connections.map((item, index) => e("div", {
                        className: "mtc-card",
                        key: item.name || index,
                        style: { animationDelay: `${index * 40}ms` }
                      },
                        e("h3", null, item.name || "Unnamed"),
                        e("div", { className: "mtc-meta" },
                          e("div", null, `Agent: ${item.agentUrl || "n/a"}`),
                          e("div", null, `Device: ${item.deviceName || "(all)"}`),
                          e("div", null, `Interval: ${item.pollIntervalMs || "?"} ms`),
                          e("div", null, `Tag Provider: ${item.tagProviderName || "n/a"}`)
                        ),
                        e("div", { className: "mtc-actions" },
                          e("button", { className: "mtc-button", onClick: () => loadIntoForm(item) }, "Edit"),
                          e("button", { className: "mtc-button", onClick: () => removeConnection(item.name) }, "Delete")
                        )
                      ))
                    )
              );
            }

            _export("MtconnectStatus", MtconnectStatus);
            _export("MtconnectConnections", MtconnectConnections);
        }
    };
});
