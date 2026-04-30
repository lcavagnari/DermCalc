import { useState } from "react";

const QUOTE = "La medicina è la scienza dell'incertezza e l'arte della probabilità. — William Osler";

const storicoItems = [
  { tipo: "PASI", valore: 8.4, severita: "Moderata", color: "#FF9800", quando: "oggi 14:32" },
  { tipo: "EASI", valore: 3.1, severita: "Lieve", color: "#4CAF50", quando: "ieri 09:15" },
  { tipo: "PASI", valore: 24.1, severita: "Severa", color: "#F44336", quando: "3 giorni fa" },
  { tipo: "BMI", valore: 22.4, severita: "Normale", color: "#4CAF50", quando: "4 giorni fa" },
];

const topbarContent = {
  home: { title: "DermCalc", sub: "Benvenuto" },
  tools: { title: "Tools", sub: "Calcolatori" },
  profilo: { title: "Profilo", sub: "Dati anagrafici" },
};

function HomeScreen({ showQuote }) {
  return (
    <div style={{ padding: 16 }}>

      {/* Fragment 1 — Benvenuto */}
      <div style={styles.fragment}>
        <p style={{ fontSize: 13, color: "#72777f", margin: "0 0 2px" }}>
          {new Date().toLocaleDateString("it-IT", { weekday: "long", day: "numeric", month: "long" })}
        </p>
        <p style={{ fontSize: 18, fontWeight: 500, color: "#1a1c1e", margin: 0 }}>
          Benvenuto, Mario
        </p>
      </div>

      {/* Fragment 2 — Citazione (solo al primo accesso) */}
      {showQuote && (
        <div style={{ ...styles.fragment, background: "#e3f3fb", borderLeft: "3px solid #006495" }}>
          <p style={{ fontSize: 12, color: "#006495", margin: 0, fontStyle: "italic", lineHeight: 1.5 }}>
            "{QUOTE}"
          </p>
        </div>
      )}

      {/* Fragment 3 — Storico */}
      <div style={styles.fragment}>
        <p style={styles.sectionLabel}>Storico recente</p>
        {storicoItems.map((item, i) => (
          <div key={i} style={styles.storicoItem}>
            <div style={{ ...styles.storicoBadge, background: item.color }}>
              <span style={{ fontSize: item.valore >= 10 ? 11 : 13, fontWeight: 700, color: "#fff" }}>
                {item.valore}
              </span>
            </div>
            <div style={{ flex: 1 }}>
              <p style={{ fontSize: 14, fontWeight: 500, margin: "0 0 2px", color: "#1a1c1e" }}>{item.tipo}</p>
              <p style={{ fontSize: 11, color: "#72777f", margin: 0 }}>{item.severita} · {item.quando}</p>
            </div>
          </div>
        ))}
      </div>

    </div>
  );
}

function ToolsScreen() {
  return (
    <div style={{ padding: 16 }}>
      <p style={styles.sectionLabel}>Indici compositi</p>
      {[
        { icon: "📊", title: "PASI", sub: "Psoriasis Area and Severity Index", chips: ["4 distretti", "0–72"], color: "#e3f3fb", textColor: "#006495" },
        { icon: "📋", title: "EASI", sub: "Eczema Area and Severity Index", chips: ["4 distretti", "0–72"], color: "#e8f5e9", textColor: "#2e7d32" },
      ].map(t => (
        <div key={t.title} style={styles.mainCard}>
          <div style={{ ...styles.mainIcon, background: t.color }}>{t.icon}</div>
          <div style={{ flex: 1 }}>
            <p style={styles.mainTitle}>{t.title}</p>
            <p style={styles.mainSub}>{t.sub}</p>
            <div style={styles.chipRow}>
              {t.chips.map(c => (
                <span key={c} style={{ ...styles.chip, background: t.color, color: t.textColor }}>{c}</span>
              ))}
            </div>
          </div>
          <span style={styles.arrow}>›</span>
        </div>
      ))}

      <p style={{ ...styles.sectionLabel, marginTop: 8 }}>Calcolatori rapidi</p>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
        {[
          { icon: "⚖️", title: "BMI", sub: "Body Mass Index" },
          { icon: "🫁", title: "BSA", sub: "Rule of Nines" },
        ].map(t => (
          <div key={t.title} style={styles.toolCard}>
            <span style={{ fontSize: 26, display: "block", marginBottom: 10 }}>{t.icon}</span>
            <p style={{ fontSize: 15, fontWeight: 500, margin: "0 0 3px", color: "#1a1c1e" }}>{t.title}</p>
            <p style={{ fontSize: 11, color: "#72777f", margin: 0 }}>{t.sub}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

function ProfiloScreen() {
  return (
    <div style={{ padding: 16 }}>
      <p style={styles.sectionLabel}>Dati anagrafici</p>
      <div style={styles.fragment}>
        {[
          { label: "Nome", value: "Mario Rossi" },
          { label: "Data di nascita", value: "14/03/1978" },
          { label: "Sesso", value: "Maschio" },
          { label: "Peso", value: "78 kg" },
          { label: "Altezza", value: "175 cm" },
        ].map(f => (
          <div key={f.label} style={{ display: "flex", justifyContent: "space-between", padding: "10px 0", borderBottom: "0.5px solid #f0f0f0" }}>
            <span style={{ fontSize: 14, color: "#72777f" }}>{f.label}</span>
            <span style={{ fontSize: 14, color: "#1a1c1e", fontWeight: 500 }}>{f.value}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

const navItems = [
  { id: "home", icon: "🏠", label: "Home" },
  { id: "tools", icon: "🔧", label: "Tools" },
  { id: "profilo", icon: "👤", label: "Profilo" },
];

export default function DermCalcMockup() {
  const [tab, setTab] = useState("home");
  const [showQuote, setShowQuote] = useState(true);

  return (
    <div style={{ display: "flex", justifyContent: "center", alignItems: "center", minHeight: "100vh", background: "#e8edf2", padding: 24 }}>
      <div style={styles.app}>
        <div style={styles.topbar}>
          <h2 style={{ fontSize: 22, fontWeight: 500, color: "#fff", margin: "0 0 2px" }}>
            {topbarContent[tab].title}
          </h2>
          <p style={{ fontSize: 13, color: "#b3d9ef", margin: 0 }}>
            {topbarContent[tab].sub}
          </p>
        </div>

        <div style={{ flex: 1, overflowY: "auto" }}>
          {tab === "home" && <HomeScreen showQuote={showQuote} />}
          {tab === "tools" && <ToolsScreen />}
          {tab === "profilo" && <ProfiloScreen />}
        </div>

        <div style={styles.bottomNav}>
          {navItems.map(n => (
            <div key={n.id} style={styles.navItem} onClick={() => setTab(n.id)}>
              <div style={{ ...styles.navIcon, background: tab === n.id ? "#e3f3fb" : "transparent" }}>
                {n.icon}
              </div>
              <span style={{ fontSize: 10, color: tab === n.id ? "#006495" : "#72777f", fontWeight: tab === n.id ? 500 : 400 }}>
                {n.label}
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

const styles = {
  app: {
    width: 360,
    height: 680,
    background: "#f4f6f8",
    borderRadius: 24,
    display: "flex",
    flexDirection: "column",
    overflow: "hidden",
    boxShadow: "0 8px 32px rgba(0,0,0,0.18)",
  },
  topbar: {
    background: "#006495",
    padding: "20px 20px 20px",
    flexShrink: 0,
  },
  sectionLabel: {
    fontSize: 11,
    fontWeight: 500,
    color: "#006495",
    letterSpacing: "0.8px",
    textTransform: "uppercase",
    margin: "0 0 10px",
  },
  fragment: {
    background: "#fff",
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    boxShadow: "0 1px 3px rgba(0,0,0,0.07)",
  },
  mainCard: {
    background: "#fff",
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    display: "flex",
    alignItems: "center",
    gap: 14,
    boxShadow: "0 1px 3px rgba(0,0,0,0.07)",
    cursor: "pointer",
  },
  mainIcon: {
    width: 48,
    height: 48,
    borderRadius: 12,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: 22,
    flexShrink: 0,
  },
  mainTitle: { fontSize: 16, fontWeight: 500, color: "#1a1c1e", margin: "0 0 2px" },
  mainSub: { fontSize: 12, color: "#72777f", margin: "0 0 8px" },
  chipRow: { display: "flex", gap: 5 },
  chip: { fontSize: 10, padding: "2px 8px", borderRadius: 6, fontWeight: 500 },
  arrow: { color: "#c0c7cf", fontSize: 22 },
  toolCard: {
    background: "#fff",
    borderRadius: 12,
    padding: 16,
    boxShadow: "0 1px 3px rgba(0,0,0,0.07)",
    cursor: "pointer",
  },
  storicoItem: {
    display: "flex",
    alignItems: "center",
    gap: 12,
    padding: "8px 0",
    borderBottom: "0.5px solid #f0f0f0",
  },
  storicoBadge: {
    width: 40,
    height: 40,
    borderRadius: 10,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    flexShrink: 0,
  },
  bottomNav: {
    background: "#fff",
    borderTop: "0.5px solid #e0e3e8",
    display: "flex",
    padding: "8px 0 12px",
    flexShrink: 0,
  },
  navItem: {
    flex: 1,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    gap: 3,
    cursor: "pointer",
  },
  navIcon: {
    width: 32,
    height: 32,
    borderRadius: 16,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: 16,
  },
};
