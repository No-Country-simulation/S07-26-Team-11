export interface KpiCardData {
  label: string;
  value: string;
  isGold: boolean;
}

export default function KpiCards({ data }: { data: KpiCardData[] }) {
  return (
    <div className="max-w-4xl mx-auto grid grid-cols-2 md:grid-cols-4 gap-4 -mt-8 relative z-10">
      {data.map((kpi, index) => (
        <div key={index} className="bg-white rounded-md shadow-sm p-5 flex flex-col">
          <p className="text-[14.48px] text-text-secondary font-medium mb-2">
            {kpi.label}
          </p>
          <p className={`text-[24.14px] sm:text-xl font-black ${kpi.isGold ? 'text-gold-dark' : 'text-text-primary'}`}>
            {kpi.value}
          </p>
        </div>
      ))}
    </div>
  );
}
