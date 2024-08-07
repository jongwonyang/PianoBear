const t = (t) => {
    if (void 0 === t) return {};
    let e =
      null !== t.acceleration && void 0 !== t.acceleration
        ? { acceleration: t.acceleration }
        : {};
    return (
      null !== t.position &&
        void 0 !== t.position &&
        (e = { ...e, position: t.position }),
      null !== t.velocity && void 0 !== t.velocity
        ? { ...e, velocity: t.velocity }
        : e
    );
  },
  e = (t, e) => {
    const { acceleration: n, position: i, timestamp: r, velocity: o } = t;
    return {
      acceleration: n,
      position: i + o * e + 0.5 * n * e ** 2,
      timestamp: r + e,
      velocity: o + n * e,
    };
  },
  n = ((t, e, n, i, r, o, s, a) =>
    class extends i {
      constructor(
        t = {},
        e = Number.NEGATIVE_INFINITY,
        n = Number.POSITIVE_INFINITY
      ) {
        super();
        const { timingProviderSource: i, vector: a } =
          void 0 === t.update
            ? { timingProviderSource: null, vector: t }
            : { timingProviderSource: t, vector: {} };
        if (
          ((this._endPosition = null === i ? n : i.endPosition),
          (this._onchange = null),
          (this._onerror = null),
          (this._onreadystatechange = null),
          (this._readyState = null === i ? "open" : i.readyState),
          (this._skew = null === i ? 0 : i.skew),
          (this._startPosition = null === i ? e : i.startPosition),
          (this._timingProviderSource = i),
          (this._timeoutId = null),
          (this._vector =
            null === i
              ? {
                  acceleration: 0,
                  position: 0,
                  velocity: 0,
                  ...r(a),
                  timestamp: o.now() / 1e3,
                }
              : i.vector),
          n < this._vector.position &&
            (this._vector = {
              ...this._vector,
              acceleration: 0,
              position: n,
              velocity: 0,
            }),
          e > this._vector.position &&
            (this._vector = {
              ...this._vector,
              acceleration: 0,
              position: e,
              velocity: 0,
            }),
          this._setInternalTimeout(),
          null === i)
        )
          s(() => this.dispatchEvent(new Event("readystatechange")));
        else {
          const t = () => {
              this._skew = i.skew;
            },
            e = () => this._setInternalVector(i.vector),
            n = () => {
              this._isAllowedTransition(i.readyState)
                ? (this._readyState = i.readyState)
                : ((this._readyState = "closed"),
                  i.removeEventListener("adjust", t),
                  i.removeEventListener("change", e),
                  i.removeEventListener("readystatechange", n)),
                null !== i.error &&
                  s(() =>
                    this.dispatchEvent(
                      new ErrorEvent("error", { error: i.error })
                    )
                  ),
                s(() => this.dispatchEvent(new Event("readystatechange")));
            };
          i.addEventListener("adjust", t),
            i.addEventListener("change", e),
            i.addEventListener("readystatechange", n);
        }
      }
      get endPosition() {
        return this._endPosition;
      }
      get onchange() {
        return null === this._onchange ? this._onchange : this._onchange[0];
      }
      set onchange(t) {
        if (
          (null !== this._onchange &&
            this.removeEventListener("change", this._onchange[1]),
          "function" == typeof t)
        ) {
          const e = t.bind(this);
          this.addEventListener("change", e), (this._onchange = [t, e]);
        } else this._onchange = null;
      }
      get onerror() {
        return null === this._onerror ? this._onerror : this._onerror[0];
      }
      set onerror(t) {
        if (
          (null !== this._onerror &&
            this.removeEventListener("error", this._onerror[1]),
          "function" == typeof t)
        ) {
          const e = t.bind(this);
          this.addEventListener("error", e), (this._onerror = [t, e]);
        } else this._onerror = null;
      }
      get onreadystatechange() {
        return null === this._onreadystatechange
          ? this._onreadystatechange
          : this._onreadystatechange[0];
      }
      set onreadystatechange(t) {
        if (
          (null !== this._onreadystatechange &&
            this.removeEventListener(
              "readystatechange",
              this._onreadystatechange[1]
            ),
          "function" == typeof t)
        ) {
          const e = t.bind(this);
          this.addEventListener("readystatechange", e),
            (this._onreadystatechange = [t, e]);
        } else this._onreadystatechange = null;
      }
      get readyState() {
        return this._readyState;
      }
      get startPosition() {
        return this._startPosition;
      }
      get timingProviderSource() {
        return this._timingProviderSource;
      }
      query() {
        if ("open" !== this._readyState) throw n();
        const t = o.now() / 1e3,
          e =
            null === this._timingProviderSource
              ? t - this._vector.timestamp
              : t + this._skew - this._vector.timestamp,
          i = a(this._vector, e);
        return this._endPosition < i.position ||
          this._startPosition > i.position
          ? (this._setInternalVector({
              ...i,
              acceleration: 0,
              position:
                this._endPosition < i.position
                  ? this._endPosition
                  : this._startPosition,
              velocity: 0,
            }),
            this.query())
          : i;
      }
      update(t) {
        if ("open" !== this._readyState) return Promise.reject(n());
        if (null !== this._timingProviderSource) {
          const e = this._timingProviderSource.update(t);
          return e instanceof Promise
            ? e
            : Promise.reject(
                new TypeError(
                  "The timingProviderSource failed to return a promise."
                )
              );
        }
        const i = r(t);
        if (0 === Object.keys(i).length) return Promise.resolve();
        const o = { ...this.query(), ...i },
          { position: s, velocity: a, acceleration: l } = o;
        return s < this._startPosition ||
          s > this._endPosition ||
          (s === this._startPosition && (a < 0 || (0 === a && l < 0))) ||
          (s === this._endPosition && (a > 0 || (0 === a && l > 0)))
          ? Promise.reject(e())
          : (this._setInternalVector(o), Promise.resolve());
      }
      _isAllowedTransition(t) {
        return (
          ("closing" === this._readyState && "closed" === t) ||
          "connecting" === this._readyState ||
          ("open" === this._readyState && ("closed" === t || "closing" === t))
        );
      }
      _setInternalTimeout() {
        if (
          (null !== this._timeoutId &&
            (clearTimeout(this._timeoutId), (this._timeoutId = null)),
          (this._endPosition === Number.POSITIVE_INFINITY &&
            this._startPosition === Number.NEGATIVE_INFINITY) ||
            (0 === this._vector.acceleration && 0 === this._vector.velocity))
        )
          return;
        const e = t(this._vector, this._startPosition, this._endPosition);
        null !== e && (this._timeoutId = s(() => this.query(), e));
      }
      _setInternalVector(t) {
        (this._vector = t),
          this._setInternalTimeout(),
          s(() => this.dispatchEvent(new Event("change")));
      }
    })(
    ((r = (
      (t) =>
      ({ acceleration: e, position: n, velocity: i }, r) => {
        const o = t(n, i, e, r);
        if (0 === o.length) return null;
        if (1 === o.length) return o[0] > 0 ? o[0] : null;
        if (2 === o.length) {
          if (o[1] < 0) return null;
          if (o[0] > 0) return o[0];
          if (o[1] > 0) return o[1];
        }
        return null;
      }
    )((t, e, n, i) => {
      if (0 === n && 0 === e) return t !== i ? [] : [0];
      if (0 === n) return [(i - t) / e];
      const r = e / n,
        o = Math.sqrt(r ** 2 - (2 / n) * (t - i));
      return [o - r, -(o + r)].filter((t) => !isNaN(t)).sort();
    })),
    (i = (t, e, n) => {
      const i = r(t, e),
        o = r(t, n);
      return null !== i && null !== o
        ? i < o
          ? i
          : o
        : null !== i
        ? i
        : null !== o
        ? o
        : null;
    }),
    (t, e, n) => {
      const r = i(t, e, n);
      return null === r || r === Number.POSITIVE_INFINITY ? null : r;
    }),
    () => {
      try {
        return new DOMException("", "IllegalValueError");
      } catch (t) {
        return (t.name = "IllegalValueError"), t;
      }
    },
    () => {
      try {
        return new DOMException("", "InvalidStateError");
      } catch (t) {
        return (t.code = 11), (t.name = "InvalidStateError"), t;
      }
    },
    ((t, e) =>
      class {
        constructor() {
          (this._listeners = new WeakMap()), (this._nativeEventTarget = t());
        }
        addEventListener(t, n, i) {
          if (null !== n) {
            let r = this._listeners.get(n);
            void 0 === r &&
              ((r = e(this, n)),
              "function" == typeof n && this._listeners.set(n, r)),
              this._nativeEventTarget.addEventListener(t, r, i);
          }
        }
        dispatchEvent(t) {
          return this._nativeEventTarget.dispatchEvent(t);
        }
        removeEventListener(t, e, n) {
          const i = null === e ? void 0 : this._listeners.get(e);
          this._nativeEventTarget.removeEventListener(
            t,
            void 0 === i ? null : i,
            n
          );
        }
      })(
      ((t) => () => {
        if (null === t)
          throw new Error("A native EventTarget could not be created.");
        return t.document.createElement("p");
      })(window),
      (t, e) => (n) => {
        const i = { value: t };
        return (
          Object.defineProperties(n, { currentTarget: i, target: i }),
          "function" == typeof e ? e.call(t, n) : e.handleEvent.call(t, n)
        );
      }
    ),
    t,
    performance,
    setTimeout,
    e
  );
var i, r;
export {
  n as TimingObject,
  t as filterTimingStateVectorUpdate,
  e as translateTimingStateVector,
};
export default null;
