#!/bin/bash

# 設定ファイルリスト
setting_files=("default_setting1.txt" "default_setting2.txt")
# シミュレーション実行回数
num_runs=10
# 結果を保存するディレクトリ
output_dir="results"

# 結果ディレクトリを作成
mkdir -p $output_dir

# 各設定ファイルごとに処理
for setting_file in "${setting_files[@]}"; do
    total=0
    setting_name=$(basename "$setting_file" .txt)

    echo "Processing $setting_file..."

    # 10回シミュレーションを実行
    for ((i=1; i<=$num_runs; i++)); do
        echo "  Run $i..."
        # シミュレーション実行、結果を一時ファイルに保存
        result=$(./one.sh -b 1 -s $setting_file) # 仮のオプション -s で設定ファイルを指定
        # 実行結果を解析して数値を取得 (ここでは1行目の数値を取得する仮定)
        value=$(echo $result | awk '{print $1}')
        total=$(echo "$total + $value" | bc)
    done

    # 平均を計算
    average=$(echo "scale=2; $total / $num_runs" | bc)

    # 結果をファイルに出力
    echo "Average result for $setting_file: $average"
    echo "$setting_name: $average" >> $output_dir/average_results.txt
done

echo "All simulations completed. Results saved in $output_dir/average_results.txt"
